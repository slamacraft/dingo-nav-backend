package com.dingdo.service.impl;

import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.GroupMsgService;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void sendGroupMsg(String robotId, String groupId, String msg) {
        BotSender sender = botManager.getBot(robotId).getSender();
        sender.SENDER.sendGroupMsg(groupId, msg);
    }

    @Override
    public String handleGroupMsg(ReqMsg reqMsg) {
        String msg = reqMsg.getMessage();

        // 不需要at的功能
        // 群管家欢迎新人时，自动发出语句，这个功能不需要at
//        if (reqMsg.getUserId().toString().equals("2854196310")) {
//            reqMsg.setRaw_message("欢迎新人");
//            return serviceFromApi.sendMsgFromApi(reqMsg);
//        }

        //没有at机器人就不回答
        if (!msg.contains("CQ:at,qq=" + reqMsg.getSelfId())) {
            return null;
        }

        // 移出at机器人的句段
        msg = this.removeAtUser(msg, reqMsg.getSelfId());
        reqMsg.setMessage(msg);

        // 没有请求什么功能，直接调用api的机器人回答它
        if (!msgTypeComponent.getUserMsgStatus(reqMsg.getUserId())) {
            String String = serviceFromApi.sendMsgFromApi(reqMsg);
            this.atSenderOnBeginning(String, reqMsg.getUserId());
            return String;
        }

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
