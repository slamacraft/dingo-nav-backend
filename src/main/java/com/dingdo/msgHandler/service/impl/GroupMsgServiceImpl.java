package com.dingdo.msgHandler.service.impl;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.extendService.otherService.SpecialReplyService;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.msgHandler.service.GroupMsgService;
import com.dingdo.util.InstructionUtils;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
public class GroupMsgServiceImpl implements GroupMsgService {

    @Autowired
    private ServiceFromApi serviceFromApi;

    @Autowired
    private BotManager botManager;

    @Autowired
    private SpecialReplyService specialReplyService;

    // 在不at的情况下，机器人对群消息产生响应的几率，默认是0
    private int RANDOM_RATIO = 0;

    private Random random = new Random();


    /**
     * 设置每次群里发送消息，不通过at机器人使机器人产生响应的几率
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @VerifiAnnotation
    @Instruction(description = "设置随机响应几率")
    public String setRandomReplyRatio(ReqMsg reqMsg, Map<String, String> params) {
        this.RANDOM_RATIO = InstructionUtils.getParamValueOfInteger(params, "randomRatio", "概率");
        return "随机响应几率已设置为" + this.RANDOM_RATIO + "%";
    }


    @Override
    public void sendGroupMsg(String robotId, String groupId, String msg) {
        BotSender sender = botManager.getBot(robotId).getSender();
        sender.SENDER.sendGroupMsg(groupId, msg);
    }


    @Override
    public String handleGroupMsg(ReqMsg reqMsg) {
        String msg = reqMsg.getRawMessage();

        /* ===========================复读模块============================ */
        specialReplyService.rereadGroupMsg(reqMsg);

        /* ===========================随机回答模块============================ */
        //没有at机器人就不回答
        if (!reqMsg.getMessage().contains("CQ:at,qq=" + reqMsg.getSelfId())) {
            if (random.nextInt(100) < RANDOM_RATIO) {
                if (random.nextInt(100) < 10) {
                    return specialReplyService.getRandomGroupMsgYesterday(reqMsg);
                } else {
                    return serviceFromApi.sendMsgFromApi(reqMsg);
                }
            }
            return null;
        }

        return atSenderOnBeginning(serviceFromApi.sendMsgFromApi(reqMsg), reqMsg.getUserId());
    }

    @Override
    public String handleMsg(ReqMsg reqMsg) {
        return this.handleGroupMsg(reqMsg);
    }

    /**
     * 在句首at某人
     *
     * @param reply
     * @param userId
     */
    private String atSenderOnBeginning(String reply, String userId) {
        return "[CQ:at,qq=" + userId + "]" + reply;
    }

    /**
     * 删除句子中的at某人
     *
     * @param msg
     * @param userId
     */
    private String removeAtUser(String msg, String userId) {
        return msg.replaceAll("\\[CQ:at,qq=" + userId + ".*?\\]", "");
    }
}
