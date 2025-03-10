package com.dingo.core.qq.model

class QQUserInfo{
    lateinit var id:String
    lateinit var username:String
    lateinit var avatar:String
    var bot: Boolean =false
    lateinit var union_openid:String
    lateinit var union_user_account:String
}

class QQChannelUserInfo{
    lateinit var user:QQUserInfo
    lateinit var nick:String
    lateinit var roles:List<String>
    lateinit var joined_at: String
}