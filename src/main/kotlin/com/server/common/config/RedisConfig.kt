package com.server.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import java.net.URI

@Configuration
class RedisConfig {

    private val redisHostName = "localhost"
    private val redisPort = 6379

    @Bean
    fun jedisPool(): JedisPool {
        return JedisPool("redis://unbox-redis.hjorof.ng.0001.aps1.cache.amazonaws.com:6379")
    }
}
