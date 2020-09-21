package com.dingdo.extendService.otherService.impl;

import com.dingdo.component.schedule.TaskRegister;
import com.dingdo.component.schedule.model.GroupMsgTaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskList;
import com.dingdo.component.schedule.model.PrivateMsgTaskInfo;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.extendService.otherService.ScheduledService;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.util.InstructionUtils;
import com.dingdo.util.nlp.NLPUtils;
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
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @VerifiAnnotation(level = VerificationEnum.FRIEND)
    @Instruction(description = "设置提醒")
    public String addRemindTask(ReqMsg reqMsg, Map<String, String> params) {
        String cron = InstructionUtils.getParamValue(params, "cron", "表达式");
        String time = InstructionUtils.getParamValue(params, "time", "时间");
        if (!StringUtils.isBlank(cron)) {
            cron = cron.replaceAll("_", " ");
        } else if (StringUtils.isNotBlank(time)) {
            cron = NLPUtils.getCronFromString(time);
        } else {
            return "至少告诉我是什么时候吧";
        }

        System.out.println("cron表达式为:" + cron);

        String message = InstructionUtils.getParamValue(params, "message", "提醒内容");
        if (StringUtils.isBlank(message)) {
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        ITaskInfo task = this.getRemindRunnable(reqMsg, message, cron);

        taskRegister.addCronTaskAndSave(task);
        return "设置成功！\n" + "你可以使用‘移除提醒’命令关闭定时提醒";
    }


    /**
     * 查看当前私聊/群聊窗口设置的定时提醒
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Instruction(description = "查看定时提醒")
    public String getAllSchedulingTask(ReqMsg reqMsg, Map<String, String> params) {
        ITaskInfo task = this.getRemindRunnable(reqMsg, null, null);
        ITaskList taskList = taskRegister.getTaskList(task);
        if (taskList == null || StringUtils.isBlank(taskList.toString())) {
            return "很抱歉，你还没有设置定时任务";
        }
        return taskList.toString();
    }


    /**
     * 移除定时提醒
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(description = "移除提醒", inMenu = false,
            errorMsg = "指令规则：\n" + "序号=【数字】")
    public String removeRemindTask(ReqMsg reqMsg, Map<String, String> params) {
        int index = InstructionUtils.getParamValueOfInteger(params, "id", "序号");

        ITaskInfo task = this.getRemindRunnable(reqMsg, null, null);
        boolean flag = taskRegister.removeCronTaskAndSave(task, index - 1);
        if (flag) {
            return "移除定时提醒成功";
        } else {
            return "好像没有设置这个提醒";
        }
    }

    /**
     * 获取定时消息提醒的任务实例
     *
     * @param reqMsg
     * @param message
     * @return
     */
    private ITaskInfo getRemindRunnable(ReqMsg reqMsg, String message, String cron) {
        String taskName = "定时提醒";

        ITaskInfo task = null;
        if (reqMsg.getMessageType().equals("private")) {
            task = new PrivateMsgTaskInfo(reqMsg, taskName, cron, message);
        } else if (reqMsg.getMessageType().equals("group")) {
            task = new GroupMsgTaskInfo(reqMsg, taskName, cron, message);
        }

        return task;
    }
}
