package com.server.common.utils
import com.server.ud.utils.UDCommonUtils.teamUnboxUserIds
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs

val allUnboxUserExperiments = listOf(
    OneCohortExperiment("TestingExperiment",
        25,
        userIdWhiteList = emptySet(),
        userIdBlackList = emptySet(),
        enableForAdmins = true),

    OneCohortExperiment("Shop_Enabled",
        0,
        userIdWhiteList = setOf("USRNAa6uOZNxPOO6AL0InF0stpke213"),
        userIdBlackList = emptySet(),
        enableForAdmins = true),

    // Enable only for user whose test credential is shared with Facebook
    OneCohortExperiment("InstagramIngestion_Enabled",
        0,
        userIdWhiteList = setOf("USREg7UVXtfVeZ4aFQSBwKmvuFa46A3"),
        userIdBlackList = emptySet(),
        enableForAdmins = false),
)

val allMultipleCohortExperiments = listOf(
    MultipleCohortExperiment(
        "SomeExperiment", cohorts = listOf(
            Pair("SomeExperiment_Control", 50),
            Pair("SomeExperiment_Enabled", 50)
        ), cohortForUnboxTeam = "SomeExperiment_Enabled"
    )
)

object ExperimentManager {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val unboxExperiments: MutableList<UnboxExperiment> = mutableListOf()
    private val multipleCohortExperiments: MutableList<MultipleCohortExperiment> = mutableListOf()

    init {
        try {
            registerExperiments(allUnboxUserExperiments)
            registerMultipleCohortExperiment(allMultipleCohortExperiments)
        } catch (e: Exception) {
            logger.error("Error initializing experiments", e)
        }
    }

    private fun registerExperiments(unboxExperiment: List<UnboxExperiment>) {
        for (e in unboxExperiment)
            unboxExperiments.add(e)
    }

    private fun registerMultipleCohortExperiment(multipleCohortExperiment: List<MultipleCohortExperiment>) {
        for (e in multipleCohortExperiment)
            multipleCohortExperiments.add(e)
    }

    fun allApplicableExperimentsMap(
        userId: String,
        isAdmin: Boolean = false,
    ): Map<String, Boolean> {
        val experimentMap = unboxExperiments.map { expt ->
            expt.getName() to expt.isEnabled(userId, isAdmin)
        }.filter { it.second }.toMap().toMutableMap()

        // Get all experiments from groups
        multipleCohortExperiments.mapNotNull { experimentGroup ->
            experimentGroup.applicableCohortFromMultipleCohorts(userId)
        }.forEach { experimentMap[it] = true }

        // Return
        return experimentMap
    }

    fun allApplicableExperimentsObject(
        userId: String,
        isAdmin: Boolean = false,
    ): AllApplicableExperiments {
        return AllApplicableExperiments (
            oneCohortExperiments = unboxExperiments.map { expt ->
                expt.getName() to expt.isEnabled(userId, isAdmin)
            }.toMap(),
            multipleCohortExperiments = multipleCohortExperiments.map { multipleCohortExperiment ->
                MultipleCohortExperimentDetail (
                    multipleCohortExperiment.getName(),
                    multipleCohortExperiment.getCohorts(),
                    multipleCohortExperiment.applicableCohortFromMultipleCohorts(userId)
                )
            }
        )
    }

    fun allMultipleCohortFromMultipleCohorts(userId: String): Map<String, String?> {
        return multipleCohortExperiments.map { eg -> eg.getName() to eg.applicableCohortFromMultipleCohorts(userId) }
            .toMap()
    }

    fun isExperimentEnabled(
        name: String,
        userId: String
    ): Boolean {
        val singleNameExperimentEnabled = unboxExperiments.filter { it.getName() == name }.firstOrNull()?.let { it.isEnabled(userId) }
            ?: false
        val cohortBasedExperimentEnabled = multipleCohortExperiments.filter { it.applicableCohortFromMultipleCohorts(userId) == name }.isNullOrEmpty().not()
        val result = singleNameExperimentEnabled || cohortBasedExperimentEnabled
        logger.info("$name experiment for userId: $userId - $result")
        return result
    }
}

abstract class UnboxExperiment(private val name: String) {
    fun getName(): String {
        return name
    }

    abstract fun isEnabled(userId: String, isAdmin: Boolean = false): Boolean

    fun assignCohort(userId: String, experimentName: String, percentage: Int): Boolean {
        val hashCode = abs((userId + experimentName).hashCode())
        return (hashCode % 100) < percentage
    }
}

class OneCohortExperiment(
    name: String,
    private val percent: Int,
    private val userIdWhiteList: Set<String> = emptySet(),
    private val userIdBlackList: Set<String> = emptySet(),
    private val enableForAdmins: Boolean = false,
) : UnboxExperiment(name) {

    override fun isEnabled(userId: String, isAdmin: Boolean): Boolean {
        if (enableForAdmins && isAdmin) return true
        if (userIdWhiteList.contains(userId)) return true
        if (userIdBlackList.contains(userId)) return false
        return assignCohort(userId, this.getName(), percent)
    }

}

open class MultipleCohortExperiment(
    private val experimentName: String,
    private val cohorts: List<Pair<String, Int>> = emptyList(),
    private val cohortForUnboxTeam: String? = null,
) {
    fun getName(): String {
        return experimentName
    }

    fun getCohorts(): Set<String> {
        return this.cohorts.map { it.first }.toSet()
    }

    open fun applicableCohortFromMultipleCohorts(userId: String): String? {
        return assignCohort(userId, experimentName, cohorts)
    }

    protected fun assignCohort(userId: String, experimentName: String, cohorts: List<Pair<String, Int>>): String? {
        if (cohortForUnboxTeam != null && teamUnboxUserIds.contains(userId)) {
            return cohortForUnboxTeam
        }
        if (cohorts.isEmpty())
            return null
        val hashCode = abs((userId + experimentName).hashCode()).rem(100)
        var prevTotal = 0
        cohorts.map {
            if (hashCode >= prevTotal && hashCode < (prevTotal + it.second))
                return it.first
            prevTotal += it.second
        }
        return null
    }
}


class AllApplicableExperiments (
    val oneCohortExperiments: Map<String, Boolean>,
    val multipleCohortExperiments: List<MultipleCohortExperimentDetail>
)

class MultipleCohortExperimentDetail (
    val experimentName: String,
    val cohortsNames: Set<String>,
    val assigned: String?
)
