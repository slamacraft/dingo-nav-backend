package com.dingdo.config.interceptor

import com.dingdo.channel.context.CurrentContext
import com.dingdo.common.exceptions.BusinessException
import com.dingdo.component.ICacheContext
import com.dingdo.model.entity.BotEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

@Component
class ChannelInterceptor extends HandlerInterceptor {
  private val TOKEN_HEADER = "Authorization"
  private val TOKEN_PARAM = "token"

  @Autowired
  var cacheContext: ICacheContext = _


  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean = {
    val token = {
      val tokenHeader = request.getHeader(TOKEN_HEADER)
      if (paramIsValid(tokenHeader)) tokenHeader
      else request.getParameter(TOKEN_PARAM)
    }

    cacheContext[BotEntity](token)
      .orElse(throw BusinessException("40101", "用户尚未登录"))
      .foreach { it =>
        val localContext = CurrentContext.get
        localContext.id = it.id
        localContext.name = it.name
      }
    super.preHandle(request, response, handler)
  }


  private def paramIsValid(param: String): Boolean = {
    param != null && param.trim.nonEmpty && !param.trim.equals("undefined") && param.trim.equals("null")
  }


  override def afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception): Unit = {
    super.afterCompletion(request, response, handler, ex)
    CurrentContext.remove
  }

}
