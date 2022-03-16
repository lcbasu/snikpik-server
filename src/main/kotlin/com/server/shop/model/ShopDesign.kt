//data class AddressV3 (
//    val id: String,
//    val fullAddress: String,
//    val street: String,
//
//    // These 3 values are required to be set when adding product.
//    // To decide if the product can be delivered to other addresses based on the distance from source of product
//    val zipcode: String?,
//    val lat: Double?,
//    val lng: Double?,
//)

//data class CompanyV3 (
//    val id: String,
//    val marketingName: String,
//    val legalName: String,
//    val dateOfEstablishment: Instant,
//    val headOfficeAddress: AddressV3,
//    val communicationAddress: AddressV3,
//    val billingAddress: AddressV3,
//    val allAddresses: List<AddressV3>,
//    val owner: List<UserV2>,
//    val admin: List<UserV2>,
//    val createdBy: UserV2,
//    val categories: List<ProductCategoryV3>,
//)

//data class Brand (
//    val id: String,
//    val logo: MediaDetailsV3,
//    val headerBanner: MediaDetailsV3,
//    val marketingName: String,
//    val legalName: String,
//    val dateOfEstablishment: Instant,
//    val company: CompanyV3,
//    val createdBy: UserV2,
//    val categories: List<ProductCategoryV3>,
//)

//data class ProductV3 (
//    val id: String,
//    val brand: Brand,
//
//    val defaultVariant: ProductVariantV3,
//
//    // Change based on the overall status of all the variants
//    var status: ProductStatusV3 = ProductStatusV3.ACTIVE,
//
//    // Unit
//    var productUnit: ProductUnitV3 = ProductUnitV3.PIECE,
//
//    val createdBy: UserV2,
//)

// We can start with only one variant for each product
// But in future we can have multiple variants for a product
// and in that case the price of the Product will be shown as
// the range of price from lowest to highest for all the variants of a product
//data class ProductVariantV3 (
//    val id: String,
//    val product: ProductV3,
//
//    var title: String,
//    val description: String,
//    val categories: List<ProductCategoryV3>,
//    val mainMediaDetails: MediaDetailsV3,
//    val specificationMediaDetails: MediaDetailsV3,
//
//    var status: ProductStatusV3 = ProductStatusV3.ACTIVE,
//
//    val createdBy: UserV2,
//
//    val viewInRoomAllowed: Boolean,
//
//    // Delivery
//    val shippedFrom: AddressV3, // Must have lat, lng and zipcode
//    val maxDeliveryDistanceInKm: Int,
//    val maxRetailPriceInPaisa: Long,
//
//    // Unit
//    var unitQuantity: Long = 0, // Like 100 ProductUnit.GRAM
//
//    // Price
//    var originalPricePerUnitInPaisa: Long = 0, // Rs 100
//    val isTaxInclusive: Boolean, // If true, then tax is included in the originalPricePerUnitInPaisa else not
//    var taxPercentagePerUnit: Double = 0.0, // 0.18 -> like 18%
//    var taxPerUnitInPaisa: Long = 0, // Rs 18 -> Rs 100 * 0.18
//    var sellingPricePerUnitInPaisa: Long = 0, // Rs 118 -> Rs 100 + Rs 18
//
//    var minOrderUnitCount: Long = 1,
//
//    // Difference between totalUnitInStock and totalSoldUnits gives the total available units
//    var totalUnitInStock: Long = 1, // Total number of units in stock -> Always keep increasing. never reset
//    var totalSoldUnits: Long = 0, // Total number of units that was sold -> Always keep increasing, never reset
//)

//open class Promotions (
//    open val id: String,
//    open val type: PromotionsType,
//    open val existenceType: ExistenceType,
//    open val discountType: ProductDiscountV3,
//    open val discountValue: Double,
//    open val maxDiscountInPaisa: Long,
//    open val startTime: Instant,
//    open val endTime: Instant,
//)

// Applies to all the product variants that are part of this brand
//data class BrandDiscount (
//    val brand: Brand,
//    override val id: String = "",
//    override val type: PromotionsType = PromotionsType.DISCOUNT,
//    override val existenceType: ExistenceType = ExistenceType.CAN_EXIST_WITH_SAME_TYPE,
//    override val discountType: DiscountTypeV3 = DiscountTypeV3.FLAT_IN_PERCENT,
//    override val discountValue: Double = 0.0, // Either percentage or flat value
//    override val maxDiscountInPaisa: Long = 0,
//    override val startTime: Instant = DateUtils.getInstantNow(),
//    override val endTime: Instant = DateUtils.getInstantNow(),
//): Promotions (id, type, existenceType, discountType, discountValue, maxDiscountInPaisa, startTime, endTime)
//
//// Applies to all the variants of the product
//data class ProductPromotions (
//    val product: ProductV3,
//    override val id: String = "",
//    override val type: PromotionsType = PromotionsType.DISCOUNT,
//    override val existenceType: ExistenceType = ExistenceType.CAN_EXIST_WITH_SAME_TYPE,
//    override val discountType: DiscountTypeV3 = DiscountTypeV3.FLAT_IN_PERCENT,
//    override val discountValue: Double = 0.0, // Either percentage or flat value
//    override val maxDiscountInPaisa: Long = 0,
//    override val startTime: Instant = DateUtils.getInstantNow(),
//    override val endTime: Instant = DateUtils.getInstantNow(),
//): Promotions (id, type, existenceType, discountType, discountValue, maxDiscountInPaisa, startTime, endTime)
//
//// Applies only to the particular variant
//data class ProductVariantPromotions (
//    val variant: ProductVariantV3,
//    override val id: String = "",
//    override val type: PromotionsType = PromotionsType.DISCOUNT,
//    override val existenceType: ExistenceType = ExistenceType.CAN_EXIST_WITH_SAME_TYPE,
//    override val discountType: DiscountTypeV3 = DiscountTypeV3.FLAT_IN_PERCENT,
//    override val discountValue: Double = 0.0, // Either percentage or flat value
//    override val maxDiscountInPaisa: Long = 0,
//    override val startTime: Instant = DateUtils.getInstantNow(),
//    override val endTime: Instant = DateUtils.getInstantNow(),
//): Promotions (id, type, existenceType, discountType, discountValue, maxDiscountInPaisa, startTime, endTime)

//data class Coupon (
//    val code: String,
//    val description: String,
//    val id: String = "",
//    val type: PromotionsType = PromotionsType.COUPON,
//    val existenceType: ExistenceType = ExistenceType.CAN_NOT_EXIST_WITH_SAME_TYPE,
//    val discountType: DiscountTypeV3 = DiscountTypeV3.FLAT_IN_PERCENT,
//    val discountValue: Double = 0.0, // Either percentage or flat value
//    val maxDiscountInPaisa: Long = 0,
//    val startTime: Instant = DateUtils.getInstantNow(),
//    val endTime: Instant = DateUtils.getInstantNow(),
//)


//data class ProductOrderV3 (
//
//    var id: String,
//
//    var type: ProductOrderType = ProductOrderType.REGULAR_ORDER,
//
//    var totalPricePayableInPaisa: Long = 0, // (priceOfProductsWithoutTaxInPaisa + totalTaxInPaisa + deliveryChargeInPaisa) - discountInPaisa
//
//    var discountInPaisa: Long, // Sum of all the applied promotions
//
//    var deliveryChargeInPaisa: Long,
//
//    var totalTaxInPaisa: Long,
//
//    var priceOfProductsWithoutTaxInPaisa: Long,
//
//    var orderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.DRAFT,
//
//    var paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.NONE,
//
//    // The ProductOrderPayment ID which was successful for this product Order
//    var successPaymentId: String?,
//
//    var deliveryAddress: AddressV3?,
//
//    // All the applied discounts and coupons
//    var promotions: List<Promotions> = emptyList(),
//
//    var createdBy: UserV2,
//
//    var cartItems: Set<CartItemV3> = emptySet(),
//)

//data class CartItemV3 (
//    val id: String,
//
//    // This cart item will ALWAYS belong to a product order
//    var productOrder: ProductOrderV3,
//
//    var product: ProductV3,
//    var productVariant: ProductVariantV3,
//
//    var totalUnits: Long,
//
//    // These 2 fields can change in future so save the values when the order is placed.
//    // Start with null. And when the value is null, we use the value of the product variant
//    var taxPerUnitInPaisaPaid: Long?,
//    var pricePerUnitInPaisaPaid: Long?,
//    var totalTaxInPaisaPaid: Long?,
//    var totalPriceWithoutTaxInPaisaPaid: Long?,
//
//    var createdBy: UserV2,
//
//    // If added to cart from a post
//    var postId: String? = null,
//)

//data class ProductOrderPaymentV3 (
//
//    var id: String,
//
//    var paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.ONLINE,
//
//    var paymentStatus: OrderPaymentStatusV3 = OrderPaymentStatusV3.STARTED,
//
//    // Actual config for Payment from UPI/ Card / PayTm etc
//    var paymentConfig: String,
//
//    var productOrder: ProductOrderV3,
//
//    var createdBy: UserV2,
//
//)

//data class ProductOrderStateChangeV3 (
//
//    var id: String,
//
//    var fromProductOrderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.DRAFT,
//    var toProductOrderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.ADDRESS_ADDED,
//
//    var stateChangeAt: LocalDateTime = DateUtils.dateTimeNow(),
//
//    var productOrderStateChangeData: String,
//
//    var productOrder: ProductOrderV3,
//
//    var createdBy: UserV2,
//)
