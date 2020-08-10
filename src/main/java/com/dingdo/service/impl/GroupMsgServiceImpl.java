package com.dingdo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.Component.VarComponent;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.GroupMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GroupMsgServiceImpl extends AbstractMsgService implements GroupMsgService {
    @Autowired
    private ServiceFromApi serviceFromApi;
    @Autowired
    private NaiveBayesComponent naiveBayesComponent;
    @Autowired
    private MsgTypeComponent msgTypeComponent;
    @Autowired
    private VarComponent varComponent;

    @Override
    public void sendGroupMsg(Long groupId, String msg) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = new JSONObject();
        json.put("message", msg);
        json.put("group_id", groupId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(UrlEnum.URL + UrlEnum.SEND_GROUP_MSG.toString(), request, String.class);
            System.out.println(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReplyMsg handleGroupMsg(ReceiveMsg receiveMsg) {
        String msg = receiveMsg.getRaw_message();

        // 不需要at的功能
        // 群管家欢迎新人时，自动发出语句，这个功能不需要at
        if (receiveMsg.getSender().getUser_id().toString().equals("2854196310")) {
            receiveMsg.setRaw_message("欢迎新人");
            return serviceFromApi.sendMsgFromApi(receiveMsg);
        }

        //没有at机器人就不回答
        if (!msg.contains("CQ:at,qq=" + varComponent.getUserId())) {
            return null;
        }

        // 移出at机器人的句段
        msg = this.removeAtUser(msg, varComponent.getUserId());

        // 没有请求什么功能，直接调用api的机器人回答它
        if (!msgTypeComponent.getUserMsgStatus(receiveMsg.getUser_id())) {
            ReplyMsg replyMsg = serviceFromApi.sendMsgFromApi(receiveMsg);
            this.atSenderOnBeginning(replyMsg, receiveMsg.getSender().getUser_id());
            return replyMsg;
        }

        // 通过分类器确定请求的功能模块,调用相对应的功能模块
        ReplyMsg replyMsg = extendServiceMap.get(naiveBayesComponent.predict(msg))
                .sendReply(receiveMsg);
        this.atSenderOnBeginning(replyMsg, receiveMsg.getSender().getUser_id());
        return replyMsg;
    }

    @Override
    public ReplyMsg handleMsg(ReceiveMsg receiveMsg) {
        // 确定用户状态
        ReplyMsg statusReply = super.determineUserStatus(receiveMsg);
        if (statusReply != null) {
            return statusReply;
        }
        return this.handleGroupMsg(receiveMsg);
    }

    /**
     * 在句首at某人
     * @param replyMsg
     * @param userId
     */
    private void atSenderOnBeginning(ReplyMsg replyMsg, Long userId){
        replyMsg.setReply("[CQ:at,qq=" + userId + "]" + replyMsg.getReply());
    }

    /**
     * 删除句子中的at某人
     * @param msg
     * @param userId
     */
    private String removeAtUser(String msg, Long userId){
        return msg.replaceAll("\\[CQ:at,qq=" + userId + "\\]", "");
    }
}
