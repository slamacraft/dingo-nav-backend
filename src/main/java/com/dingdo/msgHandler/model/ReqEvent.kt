package com.dingdo.msgHandler.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @date 2020/9/25 19:08
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
@ApiModel("请求事件dto")
data class ReqEvent(

        @ApiModelProperty("""
            事件的类型:
            memberIncrease -> 群成员增加
        """)
        var eventType: String,

        @ApiModelProperty("接收事件的机器人qq")
        var selfId: String,

        @ApiModelProperty("产生事件的群号")
        var groupId:String,

        @ApiModelProperty("事件的产生者qq")
        var userId: String,

        @ApiModelProperty("事件的处理者qq")
        var handler: String,

        @ApiModelProperty("事件消息")
        var msg: String
)