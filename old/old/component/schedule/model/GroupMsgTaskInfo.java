package com.example.old.component.schedule.model;

import com.dingdo.component.schedule.model.interfacor.IGroupTaskInfo;
import com.dingdo.component.schedule.model.interfacor.IScheduledRunnable;
import com.dingdo.config.customContext.InstructionMethodContext;
import com.dingdo.msgHandler.model.ReqMsg;
import com.example.old.msgHandler.service.impl.GroupMsgServiceImpl;
import com.dingdo.mvc.entities.MessageTaskEntity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 15:23
 * @since JDK 1.8
 */
public class GroupMsgTaskInfo implements IGroupTaskInfo, IScheduledRunnable {

    private String groupId;
    private String botId;
    private String taskName;
    private String creatorId;
    private LocalDateTime createTime;
    private String message;
    private String cron;

    public GroupMsgTaskInfo(ReqMsg reqMsg, String taskName, String cron, String message) {
        this.groupId = reqMsg.getGroupId();
        this.botId = reqMsg.getSelfId();
        this.taskName = taskName;
        this.creatorId = reqMsg.getUserId();
        this.createTime = LocalDateTime.now();
        this.message = message;
        this.cron = cron;
    }

    /**
     * 通过持久化实例构造对象
     * @param entity
     */
    public GroupMsgTaskInfo(MessageTaskEntity entity) {
        this.botId = entity.getBotId();
        this.taskName = entity.getTaskName();
        this.groupId = entity.getTargetId();
        this.creatorId = entity.getCreateBy();
        this.createTime = entity.getCreateTime();
        this.message = entity.getMessage();
        this.cron = entity.getCron();
    }

    @Override
    public String toString() {
        if (message.length() < 30) {
            return taskName + " " + message;
        }
        return taskName + " " + message.substring(0, 30) + "...";
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getBotId() {
        return botId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String getCron() {
        return cron;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GroupMsgTaskInfo)) {
            return false;
        }
        GroupMsgTaskInfo another = (GroupMsgTaskInfo) obj;
        if (this.botId.equals(another.botId)
                && this.taskName.equals(another.taskName)
                && this.creatorId.equals(another.creatorId)
                && this.groupId.equals(another.groupId)
                && this.cron.equals(another.cron)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(botId + taskName + groupId);
    }

    @Override
    public void run() {
        try {
            // 通过方法名反射调用具体的方法
            Object target = SpringContextUtils.getBean(GroupMsgServiceImpl.class);
            InstructionMethodContext.invokeMethodByName(target, "sendGroupMsg", botId, groupId, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }
}
