package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class PostTaggedProductKey: Serializable {
    @Column(name = "post_id")
    var postId: String = ""

    @Column(name = "product_id")
    var productId: String = ""
}
