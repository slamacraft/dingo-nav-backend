package com.dingdo.robot.mirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message


object MsgSender {

    fun sendMsg(target: Contact, message: Message) {
        GlobalScope.launch(Dispatchers.Default) {
            target.sendMessage(message)
        }
    }

    fun sendMsg(target: Contact, message: String) {
        GlobalScope.launch {
            target.sendMessage(message)
        }
    }

    fun sendMsg(target: MessageEvent, message: String){
        sendMsg(target.subject, message)
    }

}
