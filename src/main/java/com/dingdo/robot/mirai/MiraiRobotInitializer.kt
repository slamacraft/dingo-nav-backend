package com.dingdo.robot.mirai

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.subscribeAlways
import java.util.stream.Collectors

/**
 * @date 2020/10/12 8:53
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
object MiraiRobotInitializer {

    private var bots = HashMap<Long, Bot>()

    /**
     * 将所有的机器人登录
     */
    private suspend fun robotLogin(initBotInfo: Map<Long, String>) {
        initBotInfo.forEach{
            bots[it.key] = BotFactory.newBot(it.key, it.value){
                fileBasedDeviceInfo()
            }
        }

        // 登录所有机器人
        bots.forEach {
            it.value.alsoLogin()
        }
    }


    /**
     * 注册群消息事件[GroupMessageEvent]
     */
    fun registeredGroupMsgEvent(eventMethod: (eventType: GroupMessageEvent) -> Unit) {
        bots.values.forEach {
            val subscribeAlways = GlobalEventChannel.subscribeAlways<> { }
        }
    }

    /**
     * 注册好友消息事件[FriendMessageEvent]
     */
    fun registeredFriendMsgEvent(eventMethod: (eventType: FriendMessageEvent) -> Unit){
        bots.values.forEach {
            it.subscribeAlways<FriendMessageEvent> { eventItem ->
                eventMethod(eventItem)
            }
        }
    }

    /**
     * 更具机器人的qq号获取以及登录的机器人，当给定的qq号
     * 不在登录的机器人当中时，返回null
     */
    fun getBotInfo(id: Long): Bot? {
        return bots[id]
    }

    /**
     * 获取登录的机器人机器人列表
     */
    fun getBotList(): List<Bot> {
        return bots.values.stream().collect(Collectors.toList())
    }


    /**
     * 注册机器人并将所有机器人启动，在机器人启动完成/失败
     * 之前会将让线程休眠
     */
    fun run(initBotInfo: Map<Long, String>) {
        var flag = false;

        GlobalScope.launch {
            robotLogin(initBotInfo)
            flag = true
        }

        while (!flag) {
            Thread.sleep(1000)
        }
    }

}
