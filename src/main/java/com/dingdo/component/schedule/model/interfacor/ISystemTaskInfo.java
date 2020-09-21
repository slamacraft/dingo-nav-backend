package com.dingdo.schedule.model.interfacor;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 14:14
 * @since JDK 1.8
 */
public interface ISystemTaskInfo extends ITaskInfo{

    /**
     * 获取系统定时任务所在的class名称
     * @return
     */
    String getClassName();

    /**
     * 获取系统定时任务所在的方法名称
     * @return
     */
    String getMethodName();
}
