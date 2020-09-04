package com.dingdo.msgHandler.service.impl;

import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.PrivateMsgService;
import com.forte.qqrobot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateMsgServiceImpl implements PrivateMsgService {

    // 使用log4j打印日志
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PrivateMsgServiceImpl.class);

    private final ServiceFromApi serviceFromApi;
    private final BotManager botManager;

    @Autowired
    public PrivateMsgServiceImpl(BotManager botManager, ServiceFromApi serviceFromApi) {
        this.botManager = botManager;
        this.serviceFromApi = serviceFromApi;
    }

    @Override
    public void sendPrivateMsg(String robotId, String userId, String msg) {
        botManager.getBot(robotId)
                .getSender()
                .SENDER
                .sendPrivateMsg(userId, msg);
    }

    @Override
    public String handlePrivateMsg(ReqMsg reqMsg) {
        // 调用机器人api
        return serviceFromApi.sendMsgFromApi(reqMsg);
    }

    @Override
    public String handleMsg(ReqMsg reqMsg) {
        // 确定用户状态
        return this.handlePrivateMsg(reqMsg);
    }
}
