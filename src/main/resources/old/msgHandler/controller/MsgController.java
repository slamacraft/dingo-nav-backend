package com.dingdo.msgHandler.controller;

import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.MsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/msg")
@Api(tags = {"消息接收接口"})
public class MsgController {

    private final MsgService msgService;

    @Autowired
    public MsgController(MsgService msgService) {
        this.msgService = msgService;
    }


    @ApiOperation("消息上报接口")
    @GetMapping("/receive")
    public String receive(@RequestAttribute ReqMsg request) {
        String receive = msgService.receive(request);
        System.out.println(receive);
        return receive;
    }
}