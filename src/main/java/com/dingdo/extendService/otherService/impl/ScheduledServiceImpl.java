package com.dingdo.extendService.otherService.impl;

import com.dingdo.Component.TaskRegister;
import com.dingdo.Schedule.SchedulingRunnable;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.extendService.otherService.ScheduledService;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.impl.GroupMsgServiceImpl;
import com.dingdo.service.impl.PrivateMsgServiceImpl;
import com.dingdo.util.InstructionUtils;
import com.dingdo.util.NLPUtils;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.bot.BotManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduledServiceImpl implements ScheduledService {

    @Autowired
    private TaskRegister taskRegister;

    @Autowired
    private BotManager botManager;

    /**
     * 添加定时提醒
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @VerifiAnnotation
    @Instruction(name = "addRemindTask", description = "设置提醒")
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

        SchedulingRunnable task = this.getRemindRunnable(reqMsg, message);

        taskRegister.addCronTask(task, cron);
        return "设置成功！\n" + "你可以使用‘移除提醒’命令关闭定时提醒";
    }

    /**
     * 移除定时提醒
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Override
    @Instruction(name = "removeRemindTask", description = "移除提醒", inMenu = false)
    public String removeRemindTask(ReqMsg reqMsg, Map<String, String> params) {
        String message = InstructionUtils.getParamValue(params, "message", "提醒消息");
        if (StringUtils.isBlank(message)) {
            message = "(｡･∀･)ﾉﾞ嗨，到点了";
        }

        SchedulingRunnable task = this.getRemindRunnable(reqMsg, message);
        boolean flag = taskRegister.removeCronTask(task);
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
    private SchedulingRunnable getRemindRunnable(ReqMsg reqMsg, String message) {
        SchedulingRunnable task = null;
        if (reqMsg.getMessageType().equals("private")) {
            task = new SchedulingRunnable(PrivateMsgServiceImpl.class,
                    "sendPrivateMsg",
                    reqMsg.getSelfId(), reqMsg.getUserId(), message);
        } else if (reqMsg.getMessageType().equals("group")) {
            task = new SchedulingRunnable(GroupMsgServiceImpl.class,
                    "sendGroupMsg",
                    reqMsg.getSelfId(), reqMsg.getGroupId(), message);
        }

        return task;
    }
}
