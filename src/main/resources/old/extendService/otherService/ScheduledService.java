package com.dingo.extendService.otherService;


import com.dingo.msgHandler.model.ReqMsg;

import java.util.Map;

public interface ScheduledService {

    String addRemindTask(ReqMsg reqMsg, Map<String, String> params);

    String removeRemindTask(ReqMsg reqMsg, Map<String, String> parmas);
}
