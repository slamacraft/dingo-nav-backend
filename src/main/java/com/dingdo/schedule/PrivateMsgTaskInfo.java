package com.dingdo.schedule;

import com.dingdo.Component.InstructionMethodContext;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.schedule.entities.MessageTaskEntity;
import com.dingdo.schedule.interfacor.IPrivateTaskInfo;
import com.dingdo.schedule.interfacor.IScheduledRunnable;
import com.dingdo.msgHandler.service.impl.PrivateMsgServiceImpl;
import com.dingdo.util.SpringContextUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 15:18
 * @since JDK 1.8
 */
public class PrivateMsgTaskInfo implements IPrivateTaskInfo, IScheduledRunnable {

    private String botId;
    private String taskName;
    private String creatorId;
    private LocalDateTime createTime;
    private String message;
    private String cron;

    public PrivateMsgTaskInfo(ReqMsg reqMsg, String taskName, String cron, String message) {
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
    public PrivateMsgTaskInfo(MessageTaskEntity entity) {
        this.botId = entity.getBotId();
        this.taskName = entity.getTaskName();
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
        if (!(obj instanceof PrivateMsgTaskInfo)) {
            return false;
        }
        PrivateMsgTaskInfo another = (PrivateMsgTaskInfo) obj;
        if (this.botId.equals(another.botId)
                && this.taskName.equals(another.taskName)
                && this.creatorId.equals(another.creatorId)
                && this.cron.equals(another.cron)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(botId + taskName + creatorId);
    }

    @Override
    public void run() {
        try {
            // 通过方法名反射调用具体的方法
            Object target = SpringContextUtils.getBean(PrivateMsgServiceImpl.class);
            InstructionMethodContext.invokeMethodByName(target, "sendPrivateMsg", botId, creatorId, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getUserId() {
        return creatorId;
    }

    public String getMessage() {
        return message;
    }
}
