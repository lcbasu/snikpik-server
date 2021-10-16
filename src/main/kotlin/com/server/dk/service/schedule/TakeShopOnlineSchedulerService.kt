package com.server.dk.service.schedule

import com.server.dk.entities.Company
import com.server.dk.enums.TakeShopOnlineAfter

abstract class TakeShopOnlineSchedulerService {
    abstract fun takeShopOnline(company: Company, takeShopOnlineAfter: TakeShopOnlineAfter): Company
}
