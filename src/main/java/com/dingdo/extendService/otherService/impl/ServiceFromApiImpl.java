package com.dingdo.extendService.otherService.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.RobotAppidEnum;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.model.msgFromCQ.API_ReplyMsg;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ServiceFromApiImpl implements ServiceFromApi {

    @Autowired
    private RestTemplate restTemplate;

    JSONObject json = new JSONObject();

    @Override
    public ReplyMsg sendMsgFromApi(ReceiveMsg receiveMsg) {
        ReplyMsg replyMsg = new ReplyMsg();
        String msg = receiveMsg.getRaw_message();

        // 对啥也不说的人的回答
        if (StringUtils.isBlank(msg)) {
            replyMsg.setReply("你想对我说什么呢？");
            return replyMsg;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        json.put("spoken", msg);//请求的文本
        json.put("appid", RobotAppidEnum.getAppidByMsg(receiveMsg)); // 先使用统一的机器人pid
        json.put("userid", receiveMsg.getSender().getUser_id().toString()); //自己管理的用户id，填写可进行上下文对话

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(UrlEnum.SI_ZHI_API.toString(), request, String.class);
            API_ReplyMsg api_replyMsg = new ObjectMapper().readValue(response.getBody(), API_ReplyMsg.class);
            replyMsg.setReply(getReplyTextFromResponse(api_replyMsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replyMsg;
    }

    /**
     * 这里从api的返回中提取回答，
     * 并且在失败后将回答设置为默认的回答
     * @param api_replyMsg
     * @return
     */
    private String getReplyTextFromResponse(API_ReplyMsg api_replyMsg){
        if (api_replyMsg.getMessage().equals("success")){
            return api_replyMsg.getData().getInfo().getText();
        } else {
            return "不是很懂\n" + "（；´д｀）ゞ";
        }
    }
}
