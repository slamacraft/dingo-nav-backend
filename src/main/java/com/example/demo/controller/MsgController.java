package com.example.demo.controller;

import com.example.demo.Component.classifier.NaiveBayesComponent;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.service.MsgService;
import com.example.demo.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/msg")
public class MsgController {

    @Autowired
    MsgService msgService;

    /**
     * 消息上报接口
     *
     * @param request
     */
    @RequestMapping("/receive")
    public ReplyMsg receive(HttpServletRequest request) {
        ReplyMsg receive = msgService.receive(request);
        System.out.println(receive);
        return receive;
    }
}