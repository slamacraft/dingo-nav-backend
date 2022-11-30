package com.dingdo.config.advice

import com.dingdo.common.JsonMapper
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.{ServerHttpRequest, ServerHttpResponse}
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

import scala.beans.BeanProperty

@RestControllerAdvice
class GlobalResponseBodyAdvice extends ResponseBodyAdvice[AnyRef] {

  override def supports(returnType: MethodParameter,
                        converterType: Class[_ <: HttpMessageConverter[_]]): Boolean = true

  override def beforeBodyWrite(body: AnyRef,
                               returnType: MethodParameter,
                               selectedContentType: MediaType,
                               selectedConverterType: Class[_ <: HttpMessageConverter[_]],
                               request: ServerHttpRequest,
                               response: ServerHttpResponse): AnyRef = {
    if(body.getClass.getName.startsWith("com.dingdo.channel")){
      GlobalResp(body)
    }else{
      body
    }
  }

  class GlobalResp[T] {
    @BeanProperty
    var code = "200"
    @BeanProperty
    var message = "成功"
    @BeanProperty
    var data: T = _
  }

  object GlobalResp{
    def apply[T](bodyStr:T): GlobalResp[T] ={
      val result = new GlobalResp[T]()
      result.data = bodyStr
      result
    }
  }
}
