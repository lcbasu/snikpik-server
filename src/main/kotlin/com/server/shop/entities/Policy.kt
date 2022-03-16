package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.shop.enums.PolicyType
import javax.persistence.*

@Entity
class Policy : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var type: PolicyType = PolicyType.RETURN

    var title: String = ""
    var description: String = ""
    var mediaDetails: String = "" // MediaDetailsV3 -> String

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}
