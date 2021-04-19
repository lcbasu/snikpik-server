package com.dukaankhata.server.service.schedule

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.enums.TakeShopOnlineAfter

abstract class TakeShopOnlineSchedulerService {
    abstract fun takeShopOnline(company: Company, takeShopOnlineAfter: TakeShopOnlineAfter): Company
}
