package com.example.demo.service.impl;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.service.AbstractMsgService;
import com.example.demo.service.DissucsMsgService;
import org.springframework.stereotype.Service;

@Service
public class DissucsMsgServiceImpl extends AbstractMsgService implements DissucsMsgService {
    @Override
    public ReplyMsg handleDissucsMsg(ReceiveMsg receiveMsg) {
        return null;
    }
}
