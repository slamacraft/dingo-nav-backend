package jsonTest.test

import com.dingo.config.configuration.SlickConfig
import com.dingo.plugin.digital.model.DigitalEvent
import slick.dbio.Effect
import slick.jdbc.MySQLProfile
import slick.sql.FixedSqlStreamingAction

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.MySQLProfile.api._

object SlickTest extends App {

  val eventTable = DigitalEvent.table

  val query = eventTable.filter { it =>
    it.id === 1L && it.eventName === "事件"
  }
    .drop(10).take(5)
    .sortBy(_.id)

  val query2 = for {
    a <- eventTable
    b <- eventTable
    if a.id === b.id
  } yield (a.id, b.eventName)

  val query3 = for {
    (a, b) <- eventTable join eventTable on (_.id === _.id)
  } yield (a.id, b.eventName)

  val query4 = for {
    (a, b) <- eventTable joinLeft eventTable on (_.id === _.id)
    if a.id === 1L && a.eventName === "123"
  } yield (a.id, b.map(_.eventName))

  query.groupBy(_.id)
    .map { case (id, group) => (id, group.map(_.conditionTurn).min) }

  private val result: FixedSqlStreamingAction[Seq[DigitalEvent.EventEntity], DigitalEvent.EventEntity, Effect.Read] = query.result

  private val bd: MySQLProfile.backend.DatabaseDef = SlickConfig.DB


  Await.result(bd.run(query.result.transactionally), Duration.Inf)

  val a = a1()

  a match {
    case b: String => print(b)
    case c: Int => c + 1
    case 123 => print(123)
    case asd: Int if asd > 3 => print(asd)
  }


  def a1(): Any = {

  }
}
