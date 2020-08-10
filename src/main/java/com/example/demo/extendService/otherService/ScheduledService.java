package com.example.demo.extendService.otherService;

import com.example.demo.Schedule.SchedulingRunnable;
import com.example.demo.model.msgFromCQ.ReceiveMsg;

import java.util.Map;

public interface ScheduledService {

    String addRemindTask(ReceiveMsg receiveMsg, Map<String, String> params);

    String removeRemindTask(ReceiveMsg receiveMsg, Map<String, String> parmas);
}
