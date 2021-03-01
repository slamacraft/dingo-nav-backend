package com.example.old.extendService.otherService;


import com.dingdo.msgHandler.model.ReqMsg;

import java.util.Map;

public interface ScheduledService {

    String addRemindTask(ReqMsg reqMsg, Map<String, String> params);

    String removeRemindTask(ReqMsg reqMsg, Map<String, String> parmas);
}
