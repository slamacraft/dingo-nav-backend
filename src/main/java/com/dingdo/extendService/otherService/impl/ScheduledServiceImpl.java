package com.dingdo.extendService.otherService.impl;

import com.dingdo.Component.TaskRegister;
import com.dingdo.Schedule.SchedulingRunnable;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.extendService.otherService.ScheduledService;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScheduledServiceImpl implements ScheduledService {

    @Autowired
    private TaskRegister taskRegister;

    /**
     * 添加定时提醒
     * @param receiveMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(name = "addRemindTask", descrption = "设置提醒")
    public String addRemindTask(ReceiveMsg receiveMsg, Map<String, String> params){
        String cron = InstructionUtils.getParamValue(params, "cron", "表达式");
        if(StringUtils.isBlank(cron)){
            return "表达式不能为空哦";
        }
        cron = cron.replaceAll("_", " ");

        String message = InstructionUtils.getParamValue(params, "message", "提醒消息");
        if(StringUtils.isBlank(message)){
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        SchedulingRunnable task = this.getRemindRunnable(receiveMsg, message);

        taskRegister.addCronTask(task, cron);
        return "设置成功！";
    }

    /**
     * 移除定时提醒
     * @param receiveMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(name = "removeRemindTask", descrption = "移除提醒")
    public String removeRemindTask(ReceiveMsg receiveMsg, Map<String, String> params){
        String message = InstructionUtils.getParamValue(params, "message", "提醒消息");
        if(StringUtils.isBlank(message)){
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        SchedulingRunnable task = this.getRemindRunnable(receiveMsg, message);
        boolean flag = taskRegister.removeCronTask(task);
        if(flag){
            return "移除定时提醒成功";
        }else {
            return "好像没有设置这个提醒";
        }
    }

    /**
     * 获取定时消息提醒的任务实例
     * @param receiveMsg
     * @param message
     * @return
     */
    private SchedulingRunnable getRemindRunnable(ReceiveMsg receiveMsg, String message){
        SchedulingRunnable task = null;
        if(receiveMsg.getMessage_type().equals("private")){
            task = new SchedulingRunnable("privateMsgSeriveImpl",
                    "sendPrivateMsg",
                    receiveMsg.getUser_id(), message);
        } else if(receiveMsg.getMessage_type().equals("group")){
            task = new SchedulingRunnable("groupMsgServiceImpl",
                    "sendGroupMsg",
                    receiveMsg.getGroup_id(), message);
        }

        return task;
    }
}
