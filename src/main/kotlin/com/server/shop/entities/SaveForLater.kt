package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "save_for_later")
class SaveForLater : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id")
    var cartItem: CartItemV3? = null;

}
