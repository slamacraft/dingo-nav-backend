package com.dingo.common.collection

import java.util.concurrent.ConcurrentHashMap

/**
 * 固定大小的队列，
 */
class ConcurrentFixedQueue<T>(private val size: Int) {
    private val map = ConcurrentHashMap<T, Int>()
    private var currentSeq = 0

    fun add(t: T) {
        if (map.size >= size) {
            // 如果队列已满，则移除最老的一半元素
            map.entries.forEach {
                if (it.value <= currentSeq - size / 2) {
                    map.remove(it.key)
                }
            }
        }
        map[t] = currentSeq++
    }

    fun contains(t: T): Boolean {
        return map.containsKey(t)
    }

}