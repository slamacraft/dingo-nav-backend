package com.dingdo.extendService.model.musicFromQQ

/**
 * @date 2020/9/23 14:38
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
data class MusicQQ(
        var code: String,
        var data: MusicQQData,
        var message: String,
        var time: Long,
        var tips: String
)

data class MusicQQData(
        var keyword: String,
        var song: SongQQ
)

data class SongQQ(
        var list: List<SongList>
)

data class SongList(
        var mid: String,
        var name: String,
        var subtitle: String,
        var id: String,
        var singer: List<SingerList>
)

data class SingerList(
        var id: String,
        var mid: String,
        var name: String
)