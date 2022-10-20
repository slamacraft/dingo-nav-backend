package com.dingdo.component.schedule.model;

import com.dingdo.component.schedule.model.interfacor.IScheduledRunnable;
import com.dingdo.component.schedule.model.interfacor.ISystemTaskInfo;
import com.dingdo.config.customContext.InstructionMethodContext;
import com.dingdo.util.SpringContextUtils;

import java.time.LocalDateTime;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/28 13:50
 * @since JDK 1.8
 */
public class SystemTaskInfo implements ISystemTaskInfo, IScheduledRunnable {

    private String taskName;
    private Class targetClass;
    private String targetMethodName;
    private Object[] params;
    private LocalDateTime createTime;
    private String cron;


    public SystemTaskInfo(String taskName, Class targetClass, String targetMethodName, String cron, Object... params) {
        this.taskName = taskName;
        this.targetClass = targetClass;
        this.targetMethodName = targetMethodName;
        this.params = params;
        this.createTime = LocalDateTime.now();
        this.cron = cron;
    }


    @Override
    public String getClassName() {
        return targetClass.getName();
    }

    @Override
    public String getMethodName() {
        return targetMethodName;
    }

    @Override
    public String getTaskName() {
        return taskName;
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
    public void run() {
        try {
            // 通过方法名反射调用具体的方法
            Object target = SpringContextUtils.getBean(targetClass);
            InstructionMethodContext.invokeMethodByName(target, targetMethodName, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
