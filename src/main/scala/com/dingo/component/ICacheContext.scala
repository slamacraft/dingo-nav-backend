package com.dingo.component

import java.util.concurrent.TimeUnit
import scala.reflect.ClassTag

trait ICacheContext {

  def apply[T: ClassTag](key: String): Option[T]

  def cache[T: ClassTag](key: String, value: T, ttl: Long = -1): Unit

  def remove[T:ClassTag](key:String):Option[T]
}
