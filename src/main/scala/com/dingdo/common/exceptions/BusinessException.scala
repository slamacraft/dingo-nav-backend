package com.dingdo.common.exceptions

trait BusinessException extends RuntimeException {
  var code: String = "500"
}

object BusinessException{
  def apply(msg:String):BusinessException = new RuntimeException(msg) with BusinessException
  def apply(code:String, msg:String): BusinessException = {
    val exception = new RuntimeException(msg) with BusinessException
    exception.code = code
    exception
  }
  def apply(msg:String, e:Throwable):BusinessException = new RuntimeException(msg, e) with BusinessException
}
