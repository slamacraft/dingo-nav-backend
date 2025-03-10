package com.dingo.core.qq

import com.dingo.common.expand.sendRequestThenGetResp
import com.dingo.core.qq.model.QQChannelUserInfo
import com.dingo.core.qq.util.requestBuilder
import org.springframework.stereotype.Component

/**
 * 用户信息获取器
 */
@Component
open class QQUserInfoGetter {

    fun getChannelUserInfo(guideId:String, userId:String):QQChannelUserInfo{
       return "/guilds/${guideId}/members/${userId}".requestBuilder()
            .get().build()
            .sendRequestThenGetResp(QQChannelUserInfo::class)
    }

}