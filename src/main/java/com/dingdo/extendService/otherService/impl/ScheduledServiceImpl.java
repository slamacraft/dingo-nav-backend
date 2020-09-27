package com.dingdo.extendService.otherService.impl;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.component.schedule.TaskRegister;
import com.dingdo.component.schedule.model.GroupMsgTaskInfo;
import com.dingdo.component.schedule.model.PrivateMsgTaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskList;
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

    private final TaskRegister taskRegister;


    @Autowired
    public ScheduledServiceImpl(TaskRegister taskRegister) {
        this.taskRegister = taskRegister;
    }


    /**
     * 添加定时提醒
     * <p>
     * 通过用户的指令请求为当前聊天框设置一个定时提醒，并将该定时提醒注册到
     * {@link TaskRegister}，这个定时提醒可以是私聊也可以是群聊。输入的参数
     * 中{@code 表达式}和{@code 时间}必须有一个，如果都存在，则以{@code 表达式}
     * 为准。</p>
     *
     * <p>
     * {@code 表达式}为cron表达式，不同的是原生cron表达式的空格需要以下划线 "_"
     * 代替，以避免在解析指令参数时发生错误，例如：<br>
     * <code>
     * cron = "0 1 0 * * *"
     * this_cron = "0_1_0_*_*_*"
     * </code>
     * </p>
     *
     * <p>
     * {@code 时间}参数为简易的表示时间的中文自然语言，例如：<br>
     * 时间1 = "今天中午12点"
     * 时间2 = "星期天晚上7点30"<br>
     * 本方法会通过{@link NLPUtils}将自然语言提取为cron表达式然后执行上述操作
     * </p>
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    @Override
    @VerifiAnnotation(level = VerificationEnum.FRIEND)
    @Instruction(description = "设置提醒"
            , defaultParams = {"message=(｡･∀･)ﾉﾞ嗨，到点了"})
    public String addRemindTask(ReqMsg reqMsg, Map<String, String> params) {
        String cron = InstructionUtils.getParamValue(params, "cron", "表达式");
        String time = InstructionUtils.getParamValue(params, "time", "时间");
        String message = InstructionUtils.getParamValue(params, "message", "提醒内容");
        if (!StringUtils.isBlank(cron)) {
            cron = cron.replaceAll("_", " ");
        } else if (StringUtils.isNotBlank(time)) {
            cron = NLPUtils.getCronFromString(time);
        } else {
            return "至少告诉我是什么时候吧";
        }

        ITaskInfo task = this.getRemindRunnable(reqMsg, message, cron);

        taskRegister.addCronTaskAndSave(task);
        return "设置成功！\n" + "你可以使用‘移除提醒’命令关闭定时提醒";
    }


    /**
     * 查看当前私聊/群聊窗口设置的定时提醒
     *
     * @param reqMsg    请求消息
     * @param params    请求参数
     * @return  请求结果
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
     * @param reqMsg    请求消息
     * @param params    请求参数
     * @return  请求结果
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
     * @param reqMsg    请求消息
     * @param message   消息文本
     * @return  请求结果
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
