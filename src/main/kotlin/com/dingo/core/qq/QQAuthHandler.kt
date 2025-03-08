package com.dingo.core.qq

import com.dingo.config.post
import com.dingo.config.properties.QQProperty
import com.dingo.config.sendRequestThenGetResp
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.time.LocalDateTime

@Component
open class QQAuthHandler {
    @Autowired
    private lateinit var qqProperty: QQProperty

    // token
    private var assessToken: String = ""

    // 过期时间
    private var expiresIn: LocalDateTime = LocalDateTime.now()

    companion object {
        lateinit var instance: QQAuthHandler
    }

    init {
        instance = this
    }

    fun getAccessToken(): String {
        if (StringUtils.hasText(assessToken)
            && LocalDateTime.now().isBefore(expiresIn)
        ) {
            return assessToken
        }

        val body = mapOf(
            "appId" to qqProperty.appId,
            "clientSecret" to qqProperty.secret
        )

        val difyResp = Request.Builder()
            .url("https://bots.qq.com/app/getAppAccessToken")
            .post(body).build()
            .sendRequestThenGetResp(AssessTokenResp::class.java)

        assessToken = difyResp.access_token
        expiresIn = LocalDateTime.now().plusSeconds(difyResp.expires_in.toLong())

        return assessToken
    }

}

class AssessTokenResp {
    var access_token: String = ""
    var expires_in: String = ""
}