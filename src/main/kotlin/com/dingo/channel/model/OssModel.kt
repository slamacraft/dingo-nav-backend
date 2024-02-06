package com.dingo.channel.model

data class OssAddReq(
    val name: String,
    val size: Long,
    val url: String,
)