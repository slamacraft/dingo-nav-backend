package com.dingdo.plugin.digital.model

import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

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
  // 发生事件的前提事件
  conditionEvent: String,

  // 面是影响
  population: Int, // 人口
  military: Int, // 军力
  religion: Int, // 宗教
  wealth: Int // 财富
)

class Event(tag: Tag) extends Table[EventEntity](tag: Tag, "event") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def eventName = column[String]("event_name")

  def eventContent = column[String]("event_content")

  def conditionTurn = column[Int]("condition_turn")

  def conditionEvent = column[String]("condition_event")

  def population = column[Int]("population")

  def military = column[Int]("military")

  def religion = column[Int]("religion")

  def wealth = column[Int]("wealth")

  override def *
  = (id, eventName, eventContent, conditionTurn, conditionEvent, population, military, religion, wealth) <> (EventEntity.tupled, EventEntity.unapply)
}