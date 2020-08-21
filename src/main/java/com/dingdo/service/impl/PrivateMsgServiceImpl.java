package com.dingdo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.musicService.impl.MusicServiceImpl;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.PrivateMsgService;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PrivateMsgServiceImpl extends AbstractMsgService implements PrivateMsgService {

    // 使用log4j打印日志
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MusicServiceImpl.class);

    @Autowired
    private ServiceFromApi serviceFromApi;
    @Autowired
    private NaiveBayesComponent naiveBayesComponent;
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
        return extendServiceMap
                .get(naiveBayesComponent.predict(reqMsg.getRawMessage()))
                .sendReply(reqMsg);
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
