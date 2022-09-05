package com.dingdo.plugin.digital.model

import slick.jdbc.MySQLProfile.api._

object DigitalUser {

  // 玩家信息
  case class UserEntity
  (
    id: Long,
    alive: Boolean,
    triggeredEvent: Array[Byte], // 已触发的事件，位图

    // 拥有得资源
    population: Int = 100, // 人口
    military: Int = 100, // 军力
    religion: Int = 100, // 宗教
    wealth: Int = 100 // 财富
  )

  class UserMapper(tag: Tag) extends Table[UserEntity](tag, "digital_user") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    def alive = column[Boolean]("alive")

    def triggeredEvent = column[Array[Byte]]("triggered_event")

    def population = column[Int]("population")

    def military = column[Int]("military")

    def religion = column[Int]("religion")

    def wealth = column[Int]("wealth")

    override def * =
      (id, alive, triggeredEvent, population, military, religion, wealth) <> (UserEntity.tupled, UserEntity.unapply)
  }

  lazy val user = TableQuery[UserMapper]

  case class UserEffectEntity
  (
    id: Long,
    userId: Long,
    effectId: Long,
    turn: Int, // 持续回合

    // 面是影响
    population: Int, // 人口
    military: Int, // 军力
    religion: Int, // 宗教
    wealth: Int // 财富
  )

  class UserEffectMapper(tag: Tag) extends Table[UserEffectEntity](tag, "digital_user_effect") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    def userId = column[Long]("user_id")

    def effectId = column[Long]("effect_id")

    def turn = column[Int]("turn")

    def population = column[Int]("population")

    def military = column[Int]("military")

    def religion = column[Int]("religion")

    def wealth = column[Int]("wealth")

    override def * =
      (id, userId, effectId, turn, population, military, religion, wealth) <> (UserEffectEntity.tupled, UserEffectEntity.unapply())
  }
}
