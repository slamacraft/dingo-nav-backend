package com.dingdo.module.stopwatch

import java.time.LocalDateTime
import java.time.ZoneOffset

object StopWatchExecutor : Runnable{

    var runningFlag = true
    val lock = Object()

    override fun run() {
        while (runningFlag) {
            val stopWatchFuture: StopWatchFuture? = StopWatchHandler.get()
            if (stopWatchFuture == null) {
                synchronized(lock){ lock.wait() }
                continue
            }

            val startTime: Long = stopWatchFuture.startTime
            val now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))
            val time = startTime - now
            if (time > 0) {
                synchronized(lock) { lock.wait(time * 1000) }
                continue
            }

            val future: StopWatchFuture? = StopWatchHandler.remove()
            val task = future?.nextTask()
            task?.execute()

            StopWatchHandler.add(future!!)
        }
    }

    fun awake(){
        synchronized(lock){
            lock.notify()
        }
    }
}
