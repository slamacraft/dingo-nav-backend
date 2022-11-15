package com.dingdo.common.util

import scala.collection.mutable
import scala.util.Random

object RandomUtil {
  val random = new Random()

  def randomList(min: Int = 0, max: Int, count: Int): mutable.MutableList[Int] = {
    var countNum = count
    val recursionQueue = new mutable.Queue[() => Unit]() // 递归执行队列

    def generateRandomList(min: Int, max: Int, result: mutable.MutableList[Int]): Unit = {
      if (countNum <= 0 || max <= min) {
        return
      }
      val randomIdx = random.nextInt(max - min)
      result += min + randomIdx
      countNum -= 1

      recursionQueue += { () => generateRandomList(min, min + randomIdx - 1, result) }
      recursionQueue += { () => generateRandomList(min + randomIdx + 1, max, result) }
    }

    val result = new mutable.MutableList[Int]
    generateRandomList(min, max, result)
    while (recursionQueue.nonEmpty) {
      recursionQueue.dequeue().apply()
    }
    result
  }
}
