package com.dingo.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}

import scala.reflect.ClassTag

object JsonMapper {
  val objectMapper: ObjectMapper = new ObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def jsonToObj[T](json:String)(implicit tag:ClassTag[T]):T = {
    JsonMapper.objectMapper.readValue(json, tag.runtimeClass.asInstanceOf[Class[T]])
  }

  def objToJson[T](obj:T):String = {
    JsonMapper.objectMapper.writeValueAsString(obj)
  }
}
