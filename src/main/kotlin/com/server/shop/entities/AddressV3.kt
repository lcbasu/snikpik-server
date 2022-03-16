package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.shop.enums.AddressPOCType
import com.server.shop.enums.AddressType
import javax.persistence.*

@Entity(name = "address_v3")
class AddressV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var addressType: AddressType = AddressType.HOME

    var pocName: String = "" // Name of the person or company who needs to be contacted for this address
    @Enumerated(EnumType.STRING)
    var pocType: AddressPOCType = AddressPOCType.PERSON

    var email: String? = "" // Email address for that address

    var flatNoBuildingApartmentName: String? = null
    var streetLocality: String? = null

    var absoluteMobile: String? = "" // Phone Number with country code
    var countryCode: String? = "" // Country code


    // From Location Object
    var route: String? = null
    var locality: String? = null
    var subLocality: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var googleCode: String? = null
    var completeAddress: String = ""

    // These 3 values are required to be set when adding product.
    // To decide if the product can be delivered to other addresses based on the distance from source of product
    var zipcode: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}
