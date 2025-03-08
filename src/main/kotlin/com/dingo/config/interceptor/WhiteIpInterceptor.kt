package com.dingo.config.interceptor

import com.dingo.config.properties.SecurityProperty
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 白名单拦截器
 */
@Component
open class WhiteIpInterceptor : HandlerInterceptor {
    @Autowired
    lateinit var securityProperty: SecurityProperty

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // ip白名单校验
        if(!securityProperty.whiteIp.contains(request.remoteAddr)){
            return false
        }
        return super.preHandle(request, response, handler)
    }

}