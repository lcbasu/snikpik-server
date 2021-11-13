package com.server.common.entities

import com.server.ud.enums.ResourceType
import javax.persistence.*

@Entity
class MediaProcessingDetail : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var resourceType: ResourceType? = null
    var resourceId: String? = null

    var inputFilePath: String? = null
    var inputFilePresent: Boolean? = false

    var outputFilePath: String? = null
    var outputFilePresent: Boolean? = false

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "for_user_id")
    var forUser: User? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
