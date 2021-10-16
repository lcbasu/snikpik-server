package com.server.dk.entities

import com.server.common.enums.NotificationTokenProvider
import javax.persistence.*

@Entity
class User : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = "" // ID generated with prefix: USR

    // Only add mobile numbers when it is unique
    // Not adding a unique constraint on this field
    // as not all users would signup immediately
    // like the customers who we signup anonymously
    // without them having to do anything
    // unless they make the order
    var absoluteMobile: String? = "" // Phone Number with country code
    var countryCode: String? = "" // Country code

    @Column(unique = true)
    var uid: String? = ""
    var anonymous: Boolean = true
    var fullName: String? = ""

    var defaultAddressId: String? = "" // Address table Id

    var notificationToken: String? = ""

    @Enumerated(EnumType.STRING)
    var notificationTokenProvider: NotificationTokenProvider? = NotificationTokenProvider.FIREBASE
}
