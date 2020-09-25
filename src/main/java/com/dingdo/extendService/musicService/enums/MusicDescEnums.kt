package com.dingdo.extendService.musicService.enums

import java.util.*

/**
 * @date 2020/9/23 15:03
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
enum class MusicDescEnums(val desc: String) {
    DESC_1("生而为人，我很抱歉"),
    DESC_2("他不在对岸 我也不够勇敢"),
    DESC_3("不喜欢很多东西 所以 温暖过我的 我记得很牢"),
    DESC_4("享受一个人的安稳，又期待两个人的热闹"),
    DESC_5("再热情的心也经不起冷漠 再爱你的人也经不起冷落"),
    DESC_6("你曾经填满我的过去，却在我们约定好的未来永久缺席"),
    DESC_7("我讨厌圈钱的女孩子，可是如果是你，我更讨厌没钱的自己"),
    DESC_8("到 点 了"),
    DESC_9("“长大以后想当什么？”“小孩”"),
    DESC_10("你那么孤独，却说一个人真好。"),
    DESC_11("频频回头的人注定走不了远路。"),
    DESC_12("都是小人物，活着就就行了。"),
    DESC_13("洗澡要放歌，厕所带手机，睡觉要侧面，坐车要靠窗。"),
    DESC_14("你劝了所有人去休息，直到夜里只剩你自己。"),
    DESC_15("不能断干净又不能和好如初的感情最磨人。"),
    DESC_16("对你来说是鸡毛蒜皮，我却刻骨铭心。"),
    DESC_17("孤独的人总能感同身受，经历过绝望的人才懂绝处逢生的快乐。");

    companion object {
        private val random = Random()

        fun getRandomDesc(): String {
            val values = values()
            return values[random.nextInt(values.size)].desc
        }
    }
}