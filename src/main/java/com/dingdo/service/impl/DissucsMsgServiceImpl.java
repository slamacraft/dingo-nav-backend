package com.dingdo.service.impl;

import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.DissucsMsgService;
import org.springframework.stereotype.Service;

@Service
public class DissucsMsgServiceImpl extends AbstractMsgService implements DissucsMsgService {
    @Override
    public ReplyMsg handleDissucsMsg(ReceiveMsg receiveMsg) {
        return null;
    }
}
