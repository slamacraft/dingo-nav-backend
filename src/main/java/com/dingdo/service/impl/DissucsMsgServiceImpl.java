package com.dingdo.service.impl;


import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.AbstractMsgService;
import com.dingdo.service.DissucsMsgService;
import org.springframework.stereotype.Service;

@Service
public class DissucsMsgServiceImpl extends AbstractMsgService implements DissucsMsgService {
    @Override
    public String handleDissucsMsg(ReqMsg reqMsg) {
        return null;
    }
}
