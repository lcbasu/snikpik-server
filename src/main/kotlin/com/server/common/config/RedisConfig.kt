package com.server.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool

@Configuration
class RedisConfig {

    @Bean
    fun jedisPool(): JedisPool {
        return JedisPool("redis://unbox-redis.hjorof.ng.0001.aps1.cache.amazonaws.com:6379")
    }
}
