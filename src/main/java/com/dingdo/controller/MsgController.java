package com.dingdo.controller;

import com.dingdo.msgHandler.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/msg")
public class MsgController {

    private final MsgService msgService;

    @Autowired
    public MsgController(MsgService msgService) {
        this.msgService = msgService;
    }

    /**
     * 消息上报接口
     *
     * @param request
     */
    @RequestMapping("/receive")
    public String receive(HttpServletRequest request) {
        String receive = msgService.receive(request);
        System.out.println(receive);
        return receive;
    }
}