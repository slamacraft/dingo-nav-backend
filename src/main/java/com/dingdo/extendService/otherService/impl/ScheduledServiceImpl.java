package com.dingdo.extendService.otherService.impl;

import com.dingdo.Component.TaskRegister;
import com.dingdo.Schedule.SchedulingRunnable;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.extendService.otherService.ScheduledService;

import com.dingdo.model.msgFromMirai.ReqMsg;
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
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(name = "addRemindTask", descrption = "设置提醒")
    public String addRemindTask(ReqMsg reqMsg, Map<String, String> params){
        String cron = InstructionUtils.getParamValue(params, "cron", "表达式");
        if(StringUtils.isBlank(cron)){
            return "表达式不能为空哦";
        }
        cron = cron.replaceAll("_", " ");

        String message = InstructionUtils.getParamValue(params, "message", "提醒消息");
        if(StringUtils.isBlank(message)){
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        SchedulingRunnable task = this.getRemindRunnable(reqMsg, message);

        taskRegister.addCronTask(task, cron);
        return "设置成功！";
    }

    /**
     * 移除定时提醒
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(name = "removeRemindTask", descrption = "移除提醒")
    public String removeRemindTask(ReqMsg reqMsg, Map<String, String> params){
        String message = InstructionUtils.getParamValue(params, "message", "提醒消息");
        if(StringUtils.isBlank(message)){
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        SchedulingRunnable task = this.getRemindRunnable(reqMsg, message);
        boolean flag = taskRegister.removeCronTask(task);
        if(flag){
            return "移除定时提醒成功";
        }else {
            return "好像没有设置这个提醒";
        }
    }

    /**
     * 获取定时消息提醒的任务实例
     * @param reqMsg
     * @param message
     * @return
     */
    private SchedulingRunnable getRemindRunnable(ReqMsg reqMsg, String message){
        SchedulingRunnable task = null;
        if(reqMsg.getMessageType().equals("private")){
            task = new SchedulingRunnable("privateMsgSeriveImpl",
                    "sendPrivateMsg",
                    reqMsg.getUserId(), message);
        } else if(reqMsg.getMessageType().equals("group")){
            task = new SchedulingRunnable("groupMsgServiceImpl",
                    "sendGroupMsg",
                    reqMsg.getGroupId(), message);
        }

        return task;
    }
}
