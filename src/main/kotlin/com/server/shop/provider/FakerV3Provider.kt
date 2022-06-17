package com.server.shop.provider

import com.github.javafaker.Faker
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.UpdateUserV2LocationRequest
import com.server.common.dto.convertToString
import com.server.common.dto.toProfileTypeResponse
import com.server.common.enums.*
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.convertToString
import com.server.common.model.sampleImageMedia
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.shop.dto.SaveBrandRequest
import com.server.shop.dto.SaveCompanyV3Request
import com.server.shop.dto.SaveProductV3Request
import com.server.shop.dto.SaveProductVariantV3Request
import com.server.shop.entities.*
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductUnitV3
import com.server.shop.model.getSampleSpecificationInfoList
import com.server.shop.model.getSampleVariantInfoV3List
import com.server.shop.model.getSampleVariantProperties
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.LocationFor
import com.server.common.enums.ProcessingType
import com.server.common.enums.UserLocationUpdateType
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.user.UserV2Provider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class FakerV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val minUsersToFake = 2
    private val maxUsersToFake = 5

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var companyV3Provider: CompanyV3Provider

    @Autowired
    private lateinit var brandProvider: BrandProvider

    @Autowired
    private lateinit var productsV3Provider: ProductV3Provider

    fun doSomething(): Any {
        return "Something was done..."
    }

    fun generateFakeShopData(): Any {
        GlobalScope.launch {
            val usersV3 = generateFakeShopData_UsersV3()
            val companies = generateFakeShopData_CompanyV3(usersV3)
            val brands = generateFakeShopData_Brand(companies)
            val products = generateFakeShopData_Product(brands)

            logger.info("Fake shop data generated successfully... with ${usersV3.size} users, ${companies.size} companies, ${brands.size} brands, ${products.size} products")
        }
        return "Fake data generation started for shop"
    }

    private fun generateFakeShopData_UsersV3(): List<UserV3> {
        val result = mutableListOf<Any?>()

        val faker = Faker()

        val usersToCreate = Random.nextInt(minUsersToFake, maxUsersToFake)
        val userLocations = locationProvider.getSampleLocationRequestsFromCities(LocationFor.USER)
        val usersV2 = mutableListOf<UserV2>()
        for (i in 1..usersToCreate) {
            val profiles = ProfileType.values().toList().shuffled().take(Random.nextInt(1, ProfileType.values().size))
            val location = userLocations.shuffled().first()
            val id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.FKE.name)
            val userV2 = userV2Provider.saveUserV2(UserV2 (
                userId = "${ReadableIdPrefix.USR}$id",
                createdAt = DateUtils.getInstantNow(),
                absoluteMobile = "",
                countryCode = "",
                handle = faker.name().username(),
                dp = MediaDetailsV2(listOf(
                    SingleMediaDetail(
                        mediaUrl = "https://i.pravatar.cc/150?u=${id}",
                        mediaType = MediaType.IMAGE,
                        width = 150,
                        height = 150,
                        mediaQualityType = MediaQualityType.HIGH,
                        mimeType = "jpg"
                    )
                )).convertToString(),
                uid = id,
                anonymous = false,
                verified = Random.nextInt(1, 100) % 5 == 0,
                profiles = AllProfileTypeResponse(
                    profiles.map { it.toProfileTypeResponse() }
                ).convertToString(),
                fullName = faker.name().fullName(),
                notificationToken = null,
                notificationTokenProvider = NotificationTokenProvider.FIREBASE
            ), ProcessingType.NO_PROCESSING) ?: error("Error saving userV2 for userId: ${id}")
            // This save will also take care of creating the job to process location data
            userV2Provider.updateUserV2Location(
                UpdateUserV2LocationRequest (
                    updateTypes = setOf(UserLocationUpdateType.CURRENT, UserLocationUpdateType.PERMANENT),
                    lat = location.lat!!,
                    lng = location.lng!!,
                    zipcode = location.zipcode!!,
                    name = location.name,
                    googlePlaceId = location.googlePlaceId,
                ), userV2.userId)
            usersV2.add(userV2)
        }

        val usersV3 = mutableListOf<UserV3>()
        usersV2.map {
            usersV3.add(userV3Provider.save(it))
        }
        return usersV3
    }

    private fun generateFakeShopData_CompanyV3(users: List<UserV3>): List<CompanyV3> {

        val faker = Faker()
        val companyV3s = mutableListOf<CompanyV3>()
        users.map {
            companyV3s.add(companyV3Provider.saveCompany(
                it,
                SaveCompanyV3Request (
                    logo = sampleImageMedia.shuffled()[Random.nextInt(sampleImageMedia.size)],
                    headerBanner = sampleImageMedia.shuffled()[Random.nextInt(sampleImageMedia.size)],
                    marketingName = faker.company().name(),
                    legalName = faker.company().name(),
                    dateOfEstablishmentInSeconds = DateUtils.getEpoch(DateUtils.getDateInPast(Random.nextLong(1000, 10000))),)
            ))
        }
        return companyV3s
    }

    private fun generateFakeShopData_Brand(companies: List<CompanyV3>): List<Brand> {

        val faker = Faker()
        val brands = mutableListOf<Brand>()

        companies.map {
            brands.add(brandProvider.saveBrand(
                it.addedBy!!,
                SaveBrandRequest (
                    companyId = it.id,
                    logo = MediaDetailsV2(listOf(SingleMediaDetail(
                        mediaUrl = faker.company().logo(),
                        mediaType = MediaType.IMAGE,
                        width = 150,
                        height = 150,
                        mediaQualityType = MediaQualityType.HIGH,
                        mimeType = "png"
                    ))),
                    headerBanner = sampleImageMedia.shuffled()[Random.nextInt(sampleImageMedia.size)],
                    marketingName = faker.company().name(),
                    legalName = faker.company().name(),
                    dateOfEstablishmentInSeconds = DateUtils.getEpoch(DateUtils.getDateInPast(Random.nextLong(1000, 10000))),)
            ))
        }
        return brands
    }
    private fun generateFakeShopData_Product(brands: List<Brand>): List<ProductV3> {

        val faker = Faker()
        val products = mutableListOf<ProductV3>()

        brands.map {

            val mrp = faker.number().randomDouble(2, 100, 1000)
            val productV3Request = SaveProductV3Request (
                brandId = it.id,
                productUnit = ProductUnitV3.PIECE,
                allProductCategories = AllProductCategories(categories = ProductCategoryV3.values().toSet()),
                allProductVariants = listOf(
                    SaveProductVariantV3Request (
                        title = faker.commerce().productName(),
                        description = faker.commerce().productName(),
                        mediaDetails = sampleImageMedia.shuffled()[Random.nextInt(sampleImageMedia.size)],

                        viewInRoomAllowed = true,

                        locationId = it.addedBy!!.permanentLocationId,

                        mrpInRupees = mrp,
                        sellingPriceInRupees = mrp - (mrp * 0.1),
                        totalUnitInStock = faker.number().randomNumber(),

                        deliversOverIndia = true,

                        maxDeliveryDistanceInKm = 300,

                        replacementAcceptable = true,
                        returnAcceptable = true,
                        codAvailable = true,
                        minOrderUnitCount = 1,
                        maxOrderPerUser = -1,

                        allVariantInfos = getSampleVariantInfoV3List(),
                        allProductProperties = getSampleVariantProperties(),
                        specificationInfoList = getSampleSpecificationInfoList(),
                    )
                ),
            )

            productsV3Provider.saveProduct(it.addedBy!!, productV3Request)?.let {
                products.add(it)
            }
        }


        return products
    }
}
