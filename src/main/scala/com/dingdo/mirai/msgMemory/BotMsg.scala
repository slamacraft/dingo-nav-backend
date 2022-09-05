package com.dingdo.mirai.msgMemory

import slick.jdbc.MySQLProfile.api._

object BotMsg {

  case class MsgEntity
  (
    id: Long = 0,
    userId: Long,
    userName: String,
    groupId: Long,
    groupName: String,
    content: String
  )

  class MsgMapper(tag: Tag) extends Table[MsgEntity](tag, "msg") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def userName = column[String]("user_name")

    def groupId = column[Long]("group_id")

    def groupName = column[String]("group_name")

    def content = column[String]("content")

    override def * =
      (id, userId, userName, groupId, groupName, content) <> (MsgEntity.tupled, MsgEntity.unapply)
  }

  lazy val msg = TableQuery[MsgMapper]

}
