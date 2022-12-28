package com.dingo.component.schedule.model.interfacor;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 14:15
 * @since JDK 1.8
 */
public interface IUserTaskInfo extends ITaskInfo {

    /**
     * 获取定时任务的创建者
     *
     * @return
     */
    String getCreatorId();

    /**
     * 获取用户是通过哪个机器人来创建本定时任务
     *
     * @return
     */
    String getBotId();

}
