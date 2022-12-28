package com.dingo.channel.context


class CurrentContext {
  var id: Long = _ // 机器人qq
  var name:String = _
  var token:String = _
}

object CurrentContext {
  private val THREAD_LOCAL = ThreadLocal.withInitial(() => new CurrentContext)

  def get: CurrentContext = THREAD_LOCAL.get()

  def remove():Unit = THREAD_LOCAL.remove()
}
