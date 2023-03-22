package com.slamacraft.robot.client.mirai

import com.slamacraft.robot.client.store.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.BotConfiguration

/**
 * @date 2020/10/12 8:53
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
object MiraiRobotInitializer {

    val bots = mutableListOf<Pair<Long, Bot>>()

    /**
     * 将所有的机器人登录
     */
    private suspend fun robotLogin(initBotInfo: Map<Long, String>) {
        initBotInfo.forEach {
            login(it.key, it.value)
        }
    }

    suspend fun login(id: Long, pw: String): Bot {
        val bot = BotFactory.newBot(id, pw) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.MACOS
        }
        bot.alsoLogin()
        bots.add(id to bot)
        initGroup(bot)
        registerDefaultMsgEvent()
        return bot
    }

    private fun initGroup(bot: Bot) {
        val groups = bot.groups
        val botChatList = BotChatList()
        BotsStore.botChatsMaps[bot.id] = botChatList
        groups.forEach {
            botChatList.chatList.add(ChatInfo(it.id, it.avatarUrl, it.name))
        }
        bot.friends.forEach {
            botChatList.chatList.add(ChatInfo(it.id, it.avatarUrl, it.nick, ChatType.Friend))
        }
    }

    private fun registerDefaultMsgEvent() {
        registeredGroupMsgEvent {
            BotsStore.botChatsMaps[it.bot.id]!!.chatList.first { chatInfo ->
                chatInfo.chatId == it.group.id
            }.also { chatInfo ->
                msgToData(it.sender.avatarUrl, it.senderName, it.message).forEach { msgData ->
                    chatInfo.addMsg(msgData)
                }
            }
        }
        registeredFriendMsgEvent {
            BotsStore.botChatsMaps[it.bot.id]!!.chatList.first { chatInfo ->
                chatInfo.chatId == it.sender.id
            }.also { chatInfo ->
                msgToData(it.sender.avatarUrl, it.senderName, it.message).forEach { msgData ->
                    chatInfo.addMsg(msgData)
                }
            }
        }
    }

    private fun msgToData(headImg: String, senderName: String, messageChain: MessageChain): List<MsgData> {
        return messageChain.filterIsInstance<MessageContent>()
            .map {
                when (it) {
                    is PlainText -> TextMsgData(headImg, senderName, content = it.content)
                    is Image -> ImgMd5MsgData(headImg, senderName, md5 = it.md5)
                    else -> TextMsgData(headImg, senderName, content = it.content)
                }
            }
    }

    /**
     * 注册群消息事件[GroupMessageEvent]
     */
    fun registeredGroupMsgEvent(eventMethod: (eventType: GroupMessageEvent) -> Unit) {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> { event ->
            eventMethod(event)
        }
    }

    /**
     * 注册好友消息事件[FriendMessageEvent]
     */
    fun registeredFriendMsgEvent(eventMethod: (eventType: FriendMessageEvent) -> Unit) {
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event ->
            eventMethod(event)
        }
    }


    /**
     * 注册机器人并将所有机器人启动，在机器人启动完成/失败
     * 之前会将让线程休眠
     */
    fun run(initBotInfo: Map<Long, String>) {
        GlobalScope.launch {
            robotLogin(initBotInfo)
        }
    }

}
