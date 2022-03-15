package com.server.common.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool

@Configuration
class RedisClient {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

//    @Autowired
//    private lateinit var jedisPool: JedisPool
//
//    fun lpush(key: String?, strings: Array<String?>): Long? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.lpush(key, *strings) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in lpush", ex)
//        }
//        return null
//    }
//
//    fun lrange(key: String?, start: Long, stop: Long): List<String?>? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.lrange(key, start, stop) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in lrange", ex)
//        }
//        return LinkedList()
//    }
//
//    fun hmset(key: String?, hash: Map<String?, String?>?): String? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.hmset(key, hash) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in hmset", ex)
//        }
//        return null
//    }
//
//    fun hgetAll(key: String?): Map<String?, String?>? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.hgetAll(key) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in hgetAll", ex)
//        }
//        return HashMap()
//    }
//
//    fun sadd(key: String?, vararg members: String?): Long? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.sadd(key, *members) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in sadd", ex)
//        }
//        return null
//    }
//
//    fun smembers(key: String?): Set<String?>? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.smembers(key) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in smembers", ex)
//        }
//        return HashSet()
//    }
//
//    fun zadd(key: String?, scoreMembers: Map<String?, Double?>?): Long? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.zadd(key, scoreMembers) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in zadd", ex)
//        }
//        return 0L
//    }
//
//    fun zrange(key: String?, start: Long, stop: Long): Set<String?>? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.zrange(key, start, stop) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in zrange", ex)
//        }
//        return HashSet()
//    }
//
//    fun mset(keysValues: HashMap<String, String>): String? {
//        try {
//            jedisPool.resource.use { jedis ->
//                val keysValuesArrayList = ArrayList<String>()
//                keysValues.forEach { (key: String, value: String) ->
//                    keysValuesArrayList.add(key)
//                    keysValuesArrayList.add(value)
//                }
//                return jedis.mset(*keysValuesArrayList.toTypedArray())
//            }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in mset", ex)
//        }
//        return null
//    }
//
//    fun keys(pattern: String?): Set<String?>? {
//        try {
//            jedisPool.resource.use { jedis -> return jedis.keys(pattern) }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in keys", ex)
//        }
//        return HashSet()
//    }

//    fun set(key: String, value: String?): String? {
//        try {
//            jedisPool.resource.use {
//                return it.set(key, value)
//            }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in set", ex)
//            return null
//        }
//    }
//
//    fun get(key: String): String? {
//        try {
//            jedisPool.resource.use {
//                return it.get(key)
//            }
//        } catch (ex: Exception) {
//            logger.error("Exception caught in get", ex)
//            return null
//        }
//    }

}
