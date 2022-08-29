package com.dingdo.plugin.digital.model

import slick.jdbc.MySQLProfile.api._

object DigitalEffect {
  case class EffectEntity
  (
    id: Long,
    name: String,

    population: Int, // 人口
    military: Int, // 军力
    religion: Int, // 宗教
    wealth: Int, // 财富
  )

  class EffectMapper(tag: Tag) extends Table[EffectEntity](tag, "digital_effect") {
    def id = column[Long]("id")

    def name = column[String]("name")

    def population = column[Int]("population")

    def military = column[Int]("military")

    def religion = column[Int]("religion")

    def wealth = column[Int]("wealth")

    override def * =
      (id, name, population, military, religion, wealth) <> (EffectEntity.tupled, EffectEntity.unapply)
  }
}
