package com.dingdo.extendService.model.specialReply

import com.alibaba.fastjson.JSON
import kotlinx.serialization.json.Json

/**
 * @date 2020/9/28 9:05
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
class GroupMsgCard(var prompt: String, bannerTxt: String, summaryTxt: String, bannerImg: String) {

    var meta: GroupMsgMeta = GroupMsgMeta(GroupPushData(bannerTxt, summaryTxt, bannerImg))

    var desc = ""
    var app = "com.tencent.qqpay.qqmp.groupmsg"
    var view = "groupPushView"
    var ver = "1.0.0.7"

    override fun toString(): String {
        return "[CQ:app,content=${JSON.toJSONString(this).replace(",", "&#44;")}]"
    }
}

data class GroupMsgMeta(
        var groupPushData: GroupPushData
)

data class GroupPushData(
        var bannerTxt: String,
        var summaryTxt: String,
        var bannerImg: String
) {
    var fromIcon = ""
    var fromName = "name"
    var time = ""
    var report_url = "http://kf.qq.com/faq/180522RRRVvE180522NzuuYB.html"
    var cancel_url = "http://www.baidu.com"
    var item1Img = ""
    var bannerLink = ""
}