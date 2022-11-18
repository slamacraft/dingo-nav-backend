package com.dingdo.component.impl

import com.dingdo.common.JsonMapper
import com.dingdo.common.util.RandomUtil
import com.dingdo.component.ICacheContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.ClassTag


@Component
class LocalCacheContext extends ICacheContext {
  private val cacheMap = new mutable.HashMap[String, String]()
  private val expireCacheMap = new java.util.concurrent.ConcurrentHashMap[String, ExpireValue]()


  class ExpireValue(val value: String, val ttl: Long) {
    val expiredTime: Long = LocalDateTime.now().getLong(ChronoField.INSTANT_SECONDS) + ttl

    def isExpired: Boolean = LocalDateTime.now().getLong(ChronoField.INSTANT_SECONDS) > expiredTime
  }


  override def apply[T](key: String)(implicit tag: ClassTag[T]): Option[T] = {
    if (key == null) {
      return None
    }
    cacheMap.get(key).map {
      JsonMapper.jsonToObj(_)(tag)
    }.orElse(getExpireCache(key)(tag))
  }


  def getExpireCache[T](key: String)(implicit tag: ClassTag[T]): Option[T] = {
    Option(expireCacheMap.get(key))
      .filter { it =>
        val isExpired = it.isExpired
        if (isExpired) expireCacheMap.remove(key) // 如果过期了，还要惰性移除
        isExpired
      }
      .map(it => JsonMapper.jsonToObj(it.value)(tag))
  }


  override def cache[T: ClassTag](key: String, value: T, ttl: Long): Unit = {
    val saveValue = JsonMapper.objToJson(value)
    ttl match {
      case ttl if ttl <= 0 => cacheMap += key -> saveValue
      case _ => expireCacheMap.put(key, new ExpireValue(saveValue, ttl))
    }
  }

  @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
  def scheduledClearKey(): Unit = clearExpiredKey()

  /**
   * 每分钟清理一次过期key
   *
   * @param maxClearCount 一次最多清理的数理 默认20
   */
  @tailrec
  final def clearExpiredKey(maxClearCount: Int = 20): Unit = {
    // 随机抽取20个
    val randomIdx = RandomUtil.randomList(1, expireCacheMap.size(), maxClearCount)

    var index = 0
    val toRemoveKeys = expireCacheMap.entrySet.asScala
      .filter { it =>
        index += 1
        randomIdx.contains(index) && it.getValue.isExpired
      }.map(_.getKey)

    toRemoveKeys.foreach(expireCacheMap.remove(_)) // 移除过期的key

    if (toRemoveKeys.size >= maxClearCount / 4) { // 如果过期的比例大于 1/4，继续进行清理
      clearExpiredKey(maxClearCount)
    }
  }

  override def remove[T](key: String)(implicit tag: ClassTag[T]): Option[T] = {
    cacheMap.get(key).map { it =>
      cacheMap.remove(key)
      JsonMapper.jsonToObj(it)(tag)
    }.orElse {
      val it = getExpireCache(key)(tag)
      it.map(_ => expireCacheMap.remove(key))
      it
    }
  }
}