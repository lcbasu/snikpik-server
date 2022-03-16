package com.server.shop.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// ALL calls for customer will be routed here
// In the app, login all the people as Anonymous users the moment they visit the store

// ANONYMOUS or Logged in through Mobile both APIs
@RestController
@RequestMapping("shop/customer/v3")
class CustomerV3Controller {

}
