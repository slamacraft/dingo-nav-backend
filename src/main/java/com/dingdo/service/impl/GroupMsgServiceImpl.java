package com.dingdo.service.impl;

import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.extendService.otherService.SpecialReplyService;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.GroupMsgService;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class GroupMsgServiceImpl extends AbstractMsgService implements GroupMsgService {
    @Autowired
    private ServiceFromApi serviceFromApi;
    @Autowired
    private NaiveBayesComponent naiveBayesComponent;
    @Autowired
    private MsgTypeComponent msgTypeComponent;
    @Autowired
    private BotManager botManager;
    @Autowired
    private SpecialReplyService specialReplyService;

    private int RANDOM_RATIO = 10;

    private Random random = new Random();

    @Override
    public void sendGroupMsg(String robotId, String groupId, String msg) {
        BotSender sender = botManager.getBot(robotId).getSender();
        sender.SENDER.sendGroupMsg(groupId, msg);
    }

    @Override
    public String handleGroupMsg(ReqMsg reqMsg) {
        String msg = reqMsg.getMessage();

        ArrayList<Object> objects = new ArrayList<>();

        /* ===========================复读模块============================ */
        specialReplyService.rereadGroupMsg(reqMsg);

        /* ===========================随机回答模块============================ */
        //没有at机器人就不回答
        if (!msg.contains("CQ:at,qq=" + reqMsg.getSelfId())) {
            if(random.nextInt(100) < RANDOM_RATIO){
                return specialReplyService.getRandomGroupMsgYesterday(reqMsg);
            }
            return null;
        }

        /* ===========================api请求模块============================ */
        // 移出at机器人的句段
        msg = this.removeAtUser(msg, reqMsg.getSelfId());
        reqMsg.setMessage(msg);

        // 没有请求什么功能，直接调用api的机器人回答它
        if (!msgTypeComponent.getUserMsgStatus(reqMsg.getUserId())) {
            String String = serviceFromApi.sendMsgFromApi(reqMsg);
            this.atSenderOnBeginning(String, reqMsg.getUserId());
            return String;
        }

        /* ===========================功能请求模块============================ */
        // 通过分类器确定请求的功能模块,调用相对应的功能模块
        String reply = extendServiceMap.get(naiveBayesComponent.predict(msg))
                .sendReply(reqMsg);
        return atSenderOnBeginning(reply, reqMsg.getUserId());
    }

    @Override
    public String handleMsg(ReqMsg reqMsg) {
        // 确定用户状态
        String statusReply = super.determineUserStatus(reqMsg);
        if (statusReply != null) {
            return statusReply;
        }
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
