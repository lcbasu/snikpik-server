package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ProductCollectionKey: Serializable {
    @Column(name = "collection_id")
    var collectionId: String = ""

    @Column(name = "product_id")
    var productId: String = ""
}
