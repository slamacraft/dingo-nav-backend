package com.dingdo.extendService.model.musicFrom163

/**
 * 网抑云音乐随机歌曲api接口响应json反序列化dto
 *
 * @date 2020/9/23 14:30
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
data class Music163Random(var code: Long, var data: Music163RandomData)

data class Music163RandomData(var name: String, var url: String, var picurl: String)