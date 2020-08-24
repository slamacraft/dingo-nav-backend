package com.dingdo.service.impl;

import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.extendService.musicService.impl.MusicServiceImpl;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.PrivateMsgService;
import com.forte.qqrobot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateMsgServiceImpl extends AbstractMsgService implements PrivateMsgService {

    // 使用log4j打印日志
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MusicServiceImpl.class);

    @Autowired
    private ServiceFromApi serviceFromApi;
    @Autowired
    private MsgTypeComponent msgTypeComponent;
    @Autowired
    private BotManager botManager;

    @Override
    public void sendPrivateMsg(String robotId, String userId, String msg) {
        botManager.getBot(robotId)
                .getSender()
                .SENDER
                .sendPrivateMsg(userId, msg);
    }

    @Override
    public String handlePrivateMsg(ReqMsg reqMsg) {
        // 非功能请求状态，调用机器人api
        if (!msgTypeComponent.getUserMsgStatus(reqMsg.getUserId())) {
            return serviceFromApi.sendMsgFromApi(reqMsg);
        }

        // 功能请求状态， 调用对应的功能模块
        return null;
    }

    @Override
    public String handleMsg(ReqMsg reqMsg) {
        // 确定用户状态
        String statusReply = super.determineUserStatus(reqMsg);
        if (statusReply != null) {
            return statusReply;
        }
        return this.handlePrivateMsg(reqMsg);
    }
}
