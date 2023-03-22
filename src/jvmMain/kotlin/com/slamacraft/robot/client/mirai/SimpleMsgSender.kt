package com.slamacraft.robot.client.mirai

import com.slamacraft.robot.client.store.BotsStore
import com.slamacraft.robot.client.store.ChatType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SimpleMsgSender {

    fun sendMsg(msg: String, chatId: Long, type: ChatType) {
        if(msg.isNotBlank()){
            BotsStore.curBot?.also {
                GlobalScope.launch {
                    when (type) {
                        ChatType.Friend -> it.value!!.getFriendOrFail(chatId).sendMessage(msg)
                        ChatType.Group -> it.value!!.getGroupOrFail(chatId).sendMessage(msg)
                    }
                }
            }
        }
    }
}