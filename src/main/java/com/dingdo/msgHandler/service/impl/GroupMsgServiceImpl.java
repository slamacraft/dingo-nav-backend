package com.dingdo.msgHandler.service.impl;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.component.otherComponent.SaveGroupMsgComponent;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.extendService.otherService.SpecialReplyService;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.GroupMsgService;
import com.dingdo.util.CQCodeUtil;
import com.dingdo.util.InstructionUtils;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
public class GroupMsgServiceImpl implements GroupMsgService {

    private final ServiceFromApi serviceFromApi;
    private final BotManager botManager;
    private final SpecialReplyService specialReplyService;
    private final SaveGroupMsgComponent saveMsgComponent;

    // 在不at的情况下，机器人对群消息产生响应的几率，默认是0
    private int RANDOM_RATIO = 0;

    private final Random random = new Random();

    @Override
    public String handleMsg(ReqMsg reqMsg) {
        saveMsgComponent.saveGroupMsg(reqMsg.getRawMessage(), reqMsg.getGroupId()); // 存储群消息
        specialReplyService.rereadGroupMsg(reqMsg); // 复读消息
        if (!reqMsg.getMessage().contains("CQ:at,qq=" + reqMsg.getSelfId())) {
            return randomSendMsg(reqMsg);
        }
        return CQCodeUtil.atTarget(serviceFromApi.sendMsgFromApi(reqMsg), reqMsg.getUserId());
    }


    @Autowired
    public GroupMsgServiceImpl(SaveGroupMsgComponent saveMsgComponent,
                               ServiceFromApi serviceFromApi,
                               BotManager botManager,
                               SpecialReplyService specialReplyService) {
        this.saveMsgComponent = saveMsgComponent;
        this.serviceFromApi = serviceFromApi;
        this.botManager = botManager;
        this.specialReplyService = specialReplyService;
    }


    /**
     * 设置每次群里发送消息，不通过at机器人使机器人产生响应的几率
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    @VerifiAnnotation(level = VerificationEnum.MANAGER)
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


    /**
     * 没有at机器人就根据设定的概率随机回答
     *
     * @param reqMsg 请求消息
     * @return 响应的聊天消息
     */
    private String randomSendMsg(ReqMsg reqMsg) {
        String reply = "";
        if (!reqMsg.getMessage().contains("CQ:at,qq=" + reqMsg.getSelfId())
                && random.nextInt(100) < RANDOM_RATIO) {
            if (random.nextInt(100) < 10) {
                reply = specialReplyService.getRandomGroupMsgYesterday(reqMsg);
            } else {
                reply = serviceFromApi.sendMsgFromApi(reqMsg);
            }
        }
        return reply;
    }
}
