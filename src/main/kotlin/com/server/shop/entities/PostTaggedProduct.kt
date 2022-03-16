package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "post_tagged_product")
class PostTaggedProduct : Auditable() {
    @EmbeddedId
    var id: PostTaggedProductKey? = null

    // Keeping this so that we can directly use this field while querying through DAO
    // findAllByPostIdOpen
    @Column(name = "post_id_open")
    var postIdOpen: String = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;
}
