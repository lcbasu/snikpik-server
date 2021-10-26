package com.server.dk.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class ProductCollection : Auditable() {
    @EmbeddedId
    var id: ProductCollectionKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    var product: Product? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("collection_id")
    @JoinColumn(name = "collection_id")
    var collection: Collection? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
