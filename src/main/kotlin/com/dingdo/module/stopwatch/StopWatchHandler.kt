package com.dingdo.module.stopwatch

import java.util.*
import javax.security.auth.DestroyFailedException
import kotlin.concurrent.thread

object StopWatchHandler {
    val executor: Thread = thread {
        StopWatchExecutor.run()
    }

    val heap = PriorityQueue<StopWatchFuture>(Comparator.comparingLong { it.startTime })

    /**
     * 获取秒表任务最小堆顶部的任务实例
     * 当堆为空时，将会返回null
     *
     * @return 秒表任务实例
     */
    fun get(): StopWatchFuture? = heap.peek()


    /**
     * 新增秒表任务
     * 当新增的秒表任务执行次数不等于0时，便会将该任务插入到最小堆中，
     * 并使执行器接触休眠，使其执行当前任务或重新计算休眠时间，并返回true，
     * 如果新增的秒表任务执行次数等于0，则返回false
     * 如果执行次数等于 -1，则表示无限次执行
     *
     * @param future 新增的秒表任务
     * @return 是否新增成功
     */
    fun add(future: StopWatchFuture): Boolean {
        if (future.count == 0L) {
            return false
        }
        heap.add(future)
        synchronized(StopWatchExecutor) { StopWatchExecutor.awake() }
        return true
    }

    /**
     * 移除秒表任务
     * 将传入的秒表任务从最小堆中移除，并唤醒休眠中的执行器
     * 如果秒表任务在最小堆中存在，则返回true，
     * 否则返回false
     *
     * @param future 秒表任务实例
     * @return 是否移除成功
     */
    fun remove(future: StopWatchFuture?): Boolean {
        val result: Boolean = heap.remove(future)
        synchronized(StopWatchExecutor) { StopWatchExecutor.awake() }
        return result
    }


    /**
     * 移除最小堆的堆顶秒表任务并返回
     * 如果堆的大小为0，则返回null
     *
     * @return 堆顶的秒表任务
     */
    fun remove(): StopWatchFuture? {
        if (heap.isEmpty()) {
            return null
        }
        val result = heap.remove() as StopWatchFuture
        synchronized(StopWatchExecutor) { StopWatchExecutor.awake() }
        return result
    }


    /**
     * 终止秒表任务执行器
     *
     * @throws DestroyFailedException 终止秒表任务执行器失败
     */
    @Throws(DestroyFailedException::class)
    fun destroy() {
        executor.destroy()
        synchronized(StopWatchExecutor) { StopWatchExecutor.awake() }
    }

}
