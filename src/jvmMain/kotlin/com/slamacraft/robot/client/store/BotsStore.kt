package com.slamacraft.robot.client.store

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot

object BotsStore {
    var curBot = mutableStateOf<Bot?>(null)
    var curBotHeadImg = mutableStateOf("")
    val botChatsMaps = mutableMapOf<Long, BotChatList>()

    fun curChatList() = botChatsMaps[curBot.value?.id]
}

class BotChatList {

    val chatList = mutableStateListOf<ChatInfo>()

    fun getChat(chatId: Long) {
        chatList.first { chatId == it.chatId }
    }
}

abstract class MsgData(
    val headImg: String = "",
    val author: String,
    val isSelf: Boolean = false
)

class TextMsgData(
    headImg: String = "",
    author: String,
    isSelf: Boolean = false,
    val content: String,
) : MsgData(headImg, author, isSelf)

abstract class ImgMsgData(
    headImg: String = "",
    author: String,
    isSelf: Boolean = false
) : MsgData(headImg, author, isSelf)

class ImgUrlMsgData(
    headImg: String = "",
    author: String,
    isSelf: Boolean = false,
    val url: String,
) : ImgMsgData(headImg, author, isSelf)

class ImgMd5MsgData(
    headImg: String = "",
    author: String,
    isSelf: Boolean = false,
    val md5: ByteArray,
) : ImgMsgData(headImg, author, isSelf)

enum class ChatType {
    Friend,
    Group,
}

class ChatInfo(
    val chatId: Long,
    var headImg: String = "",
    var chatName: String = "",
    val chatType: ChatType = ChatType.Group
) {
    val unreadMsg by lazy {
        mutableStateOf(0)
    }

    lateinit var listState: LazyListState
    lateinit var coroutineScope: CoroutineScope

    val msgList by lazy {
        mutableStateListOf<MsgData>()
    }

    val inputArea by lazy {
        mutableStateOf("")
    }

    fun addMsg(msg: MsgData) {
        unreadMsg.value++
        msgList.add(msg)
        if (msgList.size - listState.firstVisibleItemIndex < 10) {
            coroutineScope.launch {
                listState.animateScrollToItem(msgList.size - 1)
            }
        }
    }
}