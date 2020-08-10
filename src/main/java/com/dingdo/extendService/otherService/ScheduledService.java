package com.dingdo.extendService.otherService;

import com.dingdo.model.msgFromCQ.ReceiveMsg;

import java.util.Map;

public interface ScheduledService {

    String addRemindTask(ReceiveMsg receiveMsg, Map<String, String> params);

    String removeRemindTask(ReceiveMsg receiveMsg, Map<String, String> parmas);
}
