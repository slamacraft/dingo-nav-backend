package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Component.MsgTypeComponent;
import com.example.demo.Component.classifier.NaiveBayesComponent;
import com.example.demo.enums.UrlEnum;
import com.example.demo.extendService.musicService.impl.MusicServiceImpl;
import com.example.demo.extendService.otherService.ServiceFromApi;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.service.AbstractMsgService;
import com.example.demo.service.PrivateMsgService;
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

    @Override
    public void sendPrivateMsg(Long userId, String msg) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = new JSONObject();
        json.put("message", msg);
        json.put("user_id", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate
                    .postForEntity(UrlEnum.URL + UrlEnum.SEND_PRIVATE_MSG.toString(), request, String.class);
            System.out.println(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg) {
        // 非功能请求状态，调用机器人api
        if (!msgTypeComponent.getUserMsgStatus(receiveMsg.getUser_id())) {
            return serviceFromApi.sendMsgFromApi(receiveMsg);
        }

        // 功能请求状态， 调用对应的功能模块
        return extendServiceMap.get(naiveBayesComponent.predict(receiveMsg.getRaw_message()))
                .sendReply(receiveMsg);
    }

    @Override
    public ReplyMsg handleMsg(ReceiveMsg receiveMsg) {
        // 确定用户状态
        ReplyMsg statusReply = super.determineUserStatus(receiveMsg);
        if (statusReply != null) {
            return statusReply;
        }
        return this.handlePrivateMsg(receiveMsg);
    }
}
