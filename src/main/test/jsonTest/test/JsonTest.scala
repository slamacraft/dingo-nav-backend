package jsonTest.test

import Json.JsonKeyConverter

import scala.collection.mutable
import scala.language.implicitConversions


object Json {

  def apply(nodes: JsonNode*): ObjectNode = {
    new ObjectNode("", nodes: _*)
  }

  def apply(key: String, nodes: JsonNode*): ObjectNode = {
    new ObjectNode(key, nodes: _*)
  }

  implicit class JsonKeyConverter(key: String) {
    def :=(value: String): StringNode = new StringNode(key, value)
    def :=(value: Int): NumberNode = new NumberNode(key, value)
    def :=(value: ObjectNode): ObjectNode = new ObjectNode(key, value.value.values.toSeq:_*)
    def :=[T <: JsonNode](value: Array[JsonNode]): ArrayNode = new ArrayNode(key, value)
  }

}

trait JsonNode extends mutable.Iterable[JsonNode]{
  type Value
  val key: String
  var value: Value

  def apply(key: String): JsonNode
}

class NoneNode extends JsonNode {
  override type Value = NoneNode
  override val key: String = "None"
  override var value: NoneNode = NoneNode()

  override def apply(key: String): JsonNode = NoneNode()

  override def iterator: Iterator[JsonNode] = List.empty.iterator
}

object NoneNode {
  private[this] val instance = new NoneNode()

  def apply(): NoneNode = {
    instance
  }
}

class StringNode(val key: String, str: String) extends JsonNode {
  override type Value = String
  override var value: String = str

  override def apply(key: String): JsonNode = NoneNode()
  override def iterator: Iterator[JsonNode] = List.empty.iterator
}

class NumberNode(val key: String, integer: Int) extends JsonNode {
  override type Value = Int
  override var value: Int = integer

  override def apply(key: String): JsonNode = NoneNode()
  override def iterator: Iterator[JsonNode] = List.empty.iterator
}

class ObjectNode(val key: String, nodes: JsonNode*) extends JsonNode {
  override type Value = mutable.HashMap[String, JsonNode]
  override var value = new mutable.HashMap[String, JsonNode]()

  for (node <- nodes) {
    value(node.key) = node
  }

  override def apply(key: String): JsonNode = {
    value.getOrElse(key, NoneNode())
  }
  override def iterator: Iterator[JsonNode] = value.values.iterator
}

class ArrayNode(val key: String, nodes: Array[JsonNode]) extends JsonNode {
  override type Value = mutable.MutableList[JsonNode]
  override var value = new mutable.MutableList[JsonNode]()

  for (node <- nodes) {
    value += node
  }

  override def apply(key: String): JsonNode = NoneNode()

  override def iterator: Iterator[JsonNode] = value.iterator
}

object JsonTest extends App {

  val json = Json(
    "key1" := "123",
    "key2" := 2,

    "obj" := Json(
      "123" := 123213,
      "key123" := "1231231"
    ),

    "123123123" := 11111,

    "array" := Array(
      "123" := 123123,
      "2313123" := 123123123
    )
  )

  val value1 = Array("123" := 123123)

  val value = json("obj")("123").value
  println(value)

  val xml = <a>
    <asdsa>123123123</asdsa>
  </a>
}