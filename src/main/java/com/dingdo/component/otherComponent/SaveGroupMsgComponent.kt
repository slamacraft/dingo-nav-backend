package com.dingdo.component.otherComponent

import com.dingdo.common.annotation.Instruction
import com.dingdo.common.annotation.VerifiAnnotation
import com.dingdo.enums.VerificationEnum
import com.dingdo.msgHandler.model.ReqMsg
import com.dingdo.util.FileUtils
import com.dingdo.util.InstructionUtils
import com.forte.qqrobot.anno.Async
import org.apache.log4j.Logger
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * @date 2020/9/25 20:17
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
@Component
open class SaveGroupMsgComponent : DisposableBean {

    private val logger = Logger.getLogger(SaveGroupMsgComponent::class.java)

    // 消息缓存池 key:groupId value:msg
    private val msgMap = mutableMapOf<String, StringBuilder>()

    // 消息缓冲池大小
    private var bufferSize = 100


    /**
     * 通过指令动态设置消息缓存区[bufferSize]的大小，大小参数保存再[params]中
     * 如果设置成功，返回设置成功的消息
     */
    @VerifiAnnotation(level = VerificationEnum.DEVELOPER)
    @Instruction(description = "设置消息缓存", errorMsg = "参数格式: 缓存大小=[数字 <= INT.MAX]")
    fun setMsgListSize(reqMsg: ReqMsg, params: Map<String, String>): String {
        this.bufferSize = InstructionUtils.getParamValueOfInteger(params, "bufferSize", "缓存大小")
        return "消息缓存大小设置成功！";
    }


    /**
     *  保存群聊消息的方法
     *  通过[appendMsg]方法将消息保存至[msgMap]中
     *  如果指定[groupId]群的群聊消息的字符长度>=[bufferSize]
     *  则通过[writeMsgToFile]异步保存群聊消息
     */
    fun saveGroupMsg(msg: String, groupId: String) {
        if (appendMsg(msg, groupId).length >= bufferSize) {
            writeMsgToFile(msgMap[groupId].toString(), groupId)
            msgMap[groupId]!!.clear()
        }
    }


    /**
     * 将消息保存到[msgMap]中
     * 如果[msgMap]已存在消息缓存，则将消息加入[msgMap]中
     * 如果不存在，则新建立一个[StringBuilder]作为消息缓存，并将
     * 消息保存至缓存中
     */
    private fun appendMsg(msg: String, groupId: String): String {
        if (msgMap[groupId] == null) {
            msgMap[groupId] = StringBuilder()
        }
        msgMap[groupId]!!.append("${msg}\r\n")
        return msgMap[groupId].toString()
    }


    /**
     * 异步保存消息的方法
     * 将[msg]保存名为"{[groupId] yyyy-MM-dd}.txt"的文件中
     */
    @Async
    private fun writeMsgToFile(msg: String, groupId: String) {
        val today = LocalDate.now()
        logger.info("已储存群${groupId}的消息")
        FileUtils.appendTextRelativeToJar("/message/${groupId} $today.txt", msg)
    }


    /**
     * 销毁方法
     * 清空缓存池并存储所有的消息
     */
    override fun destroy() {
        msgMap.forEach {
            writeMsgToFile(it.value.toString(), it.key)
        }
    }

}