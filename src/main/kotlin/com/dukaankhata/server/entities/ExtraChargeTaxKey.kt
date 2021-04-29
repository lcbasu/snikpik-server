package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.TaxType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ExtraChargeTaxKey: Serializable {
    @Column(name = "company_id")
    var companyId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type")
    var taxType: TaxType = TaxType.GST
}
