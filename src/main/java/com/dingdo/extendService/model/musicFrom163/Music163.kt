package com.dingdo.extendService.model.musicFrom163

/**
 *  网抑云音乐api查询得到的响应json反序列化dto
 * @date 2020/9/23 14:24
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
data class Music163(var code: String, var msg: String, var data: Music163Data)

data class Music163Data(var songs: List<Music163Song>, var songCount: Long)

data class Music163Song(var id: String, var name: String)