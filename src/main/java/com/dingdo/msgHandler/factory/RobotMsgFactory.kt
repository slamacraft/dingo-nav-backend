package com.dingdo.msgHandler.factory

import com.dingdo.common.exception.CQCodeConstructException
import com.dingdo.enums.CQCodeEnum
import com.dingdo.msgHandler.model.CQCode
import com.dingdo.msgHandler.model.ReqEvent
import com.dingdo.msgHandler.model.ReqMsg
import com.dingdo.util.CQCodeUtil
import com.dingdo.util.InstructionUtils
import com.forte.qqrobot.beans.messages.NicknameAble
import com.forte.qqrobot.beans.messages.RemarkAble
import com.forte.qqrobot.beans.messages.msgget.GroupMsg
import com.forte.qqrobot.beans.messages.msgget.MsgGet
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg
import org.apache.log4j.Logger
import java.util.*
import java.util.regex.Pattern

/**
 * @date 2020/9/25 18:12
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */

object RobotMsgFactory {

    val logger: Logger = Logger.getLogger(RobotMsgFactory::class.java)


    /**
     * 根据simple-bot的消息，将其转换为[ReqMsg]后返回
     */
    @JvmStatic
    fun createReqMsg(msg: Any): ReqMsg {
        val reqMsg = ReqMsg()
        msg as MsgGet
        reqMsg.message = msg.msg
        reqMsg.font = msg.font
        reqMsg.time = msg.time
        reqMsg.messageId = msg.id
        reqMsg.selfId = msg.thisCode
        reqMsg.rawMessage = CQCodeUtil.removeAllCQCode(msg.msg)

        msg as NicknameAble
        reqMsg.nickname = msg.nickname

        msg as RemarkAble
        reqMsg.card = msg.remark

        if (msg is PrivateMsg) {
            reqMsg.messageType = "private"
            reqMsg.userId = msg.qq
        } else if (msg is GroupMsg) {
            reqMsg.messageType = "group"
            reqMsg.userId = msg.qq
            reqMsg.groupId = msg.group
        }

        return reqMsg
    }


    /**
     * 将语句中的CQ码拆解出来，并封装为[CQCode]的List集合
     */
    @JvmStatic
    fun getCQCodeList(msg: String): List<CQCode> {
        val pattern = Pattern.compile("\\[CQ:.*?]")
        val matcher = pattern.matcher(msg)
        val result = LinkedList<CQCode>()

        while (matcher.find()) {
            val cqCode = matcher.group()
            try {
                result.add(getCQCodeInstance(cqCode))
            } catch (e: CQCodeConstructException) {
                logger.error("CQ码提取异常$cqCode", e)
            }
        }

        return result
    }


    /**
     * 将以“[CQ:”开头的，“]”结尾的CQ码拆解为[CQCode]对象
     * 如果拆解失败，抛出[CQCodeConstructException]异常
     */
    @JvmStatic
    fun getCQCodeInstance(cqCode: String): CQCode {
        val code = cqCode.replace("\\[CQ:|]", "").split(",")
        if (code.size < 2) {
            throw CQCodeConstructException("提取CQ码失败")
        }

        val result = CQCode()
        result.code = CQCodeEnum.getCQCode(code[0].replace("[CQ:", ""))
        result.values = InstructionUtils.analysisInstruction(*code.toTypedArray())
        return result
    }

}