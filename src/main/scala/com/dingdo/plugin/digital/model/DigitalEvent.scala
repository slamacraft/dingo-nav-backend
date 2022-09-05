package com.dingdo.plugin.digital.model

import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag


object DigitalEvent {

  /**
   * 游戏的事件
   */
  case class EventEntity
  (
    id: Long,
    eventName: String,
    eventContent: String,

    // 发生事件的最少回合数
    conditionTurn: Int,
    // 发生事件的前提事件，实际上是类似位图的结构
    // 0-不关注 1-需要同意 2-需要反对
    conditionEvent: String,

    // 面是直接造成的影响，一次性的，比如扣除财富，削减人口
    population: Int, // 人口
    military: Int, // 军力
    religion: Int, // 宗教
    wealth: Int, // 财富

    // 下面是间接造成的影响，会持续一段时间
    // 同样也是个类似位图的结构
    // 0-无此影响 1-增加影响 2-去除影响
    effect: String
  )

  class EventMapper(tag: Tag) extends Table[EventEntity](tag: Tag, "digital_event") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def eventName = column[String]("event_name")

    def eventContent = column[String]("event_content")

    def conditionTurn = column[Int]("condition_turn")

    def conditionEvent = column[String]("condition_event")

    def population = column[Int]("population")

    def military = column[Int]("military")

    def religion = column[Int]("religion")

    def wealth = column[Int]("wealth")

    def effect = column[String]("effect")

    override def *
    = (id, eventName, eventContent, conditionTurn, conditionEvent, population, military, religion, wealth, effect) <> (EventEntity.tupled, EventEntity.unapply)
  }

  lazy val table = TableQuery[EventMapper]
}

