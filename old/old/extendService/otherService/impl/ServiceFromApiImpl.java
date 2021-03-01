package com.example.old.extendService.otherService.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.model.MsgFromSiZhi.ChatMsg;
import com.example.old.extendService.otherService.ServiceFromApi;
import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.factory.BotDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.dingdo.robot.enums.MsgTypeEnum.PRIVATE;


@Service
public class ServiceFromApiImpl implements ServiceFromApi {

    private static final Logger logger = Logger.getLogger(ServiceFromApiImpl.class);

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public ReplyMsg sendMsgFromApi(ReqMsg reqMsg) {
        String msg = reqMsg.getMsg();
        JSONObject json = new JSONObject();

        // 对啥也不说的人的回答
        if (StringUtils.isBlank(msg)) {
            return PRIVATE.equals(reqMsg.getType())
                    ? BotDtoFactory.replyMsg("你想对我说什么呢？")
                    : BotDtoFactory.replyMsg("");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        json.put("spoken", msg);//请求的文本
        json.put("appid", "cebaf94c551f180d5c6847cf1ccaa1fa"); // 先使用统一的机器人pid
        json.put("userid", reqMsg.getUserId()); //自己管理的用户id，填写可进行上下文对话

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(UrlEnum.SI_ZHI_API.toString(), request, String.class);
            ChatMsg api_String = new ObjectMapper().readValue(response.getBody(), ChatMsg.class);
            return BotDtoFactory.replyMsg(getReplyTextFromResponse(api_String));
        } catch (Exception e) {
            logger.error(e);
        }

        return BotDtoFactory.replyMsg("不是很懂\n" + "（；´д｀）ゞ");
    }


    /**
     * 这里从api的返回中提取回答，
     * 并且在失败后将回答设置为默认的回答
     *
     * @param chatMsg
     * @return
     */
    private String getReplyTextFromResponse(ChatMsg chatMsg) {
        if (chatMsg.getMessage().equals("success")) {
            return chatMsg.getData().getInfo().getText();
        } else {
            return "不是很懂\n" + "（；´д｀）ゞ";
        }
    }
}
