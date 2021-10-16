package com.dukaankhata.server.entities

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.LocalDate

sealed class Moment

data class WeekDay(
    val dayOfWeek: DayOfWeek
) : Moment()

data class ExceptionDate(
    val date: LocalDate,
) : Moment()

// https://xebia.com/blog/designing-your-dynamodb-tables-efficiently-and-modelling-mixed-data-types-with-kotlin/
class MomentConverter : DynamoDBTypeConverter<String?, Moment?> {

    override fun convert(moment: Moment?): String? {
        return moment?.let {
            when (it) {
                is WeekDay -> it.dayOfWeek.toString()
                is ExceptionDate -> it.date.toString()
            }
        }
    }

    override fun unconvert(momentAsString: String?): Moment? {
        return momentAsString?.let {
            try {
                ExceptionDate(
                    date = LocalDate.parse(it)
                )
            } catch (e: Exception) {
                try {
                    WeekDay(
                        dayOfWeek = DayOfWeek.valueOf(momentAsString)
                    )
                } catch (e: Exception) {
                    logger.error("Could not parse input '$momentAsString' to Moment.", e)
                    null
                }
            }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
