package com.dingdo.schedule.model.interfacor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 14:02
 * @since JDK 1.8
 */
public interface ITaskInfo extends Serializable {

    /**
     * 获取定时任务的名称
     * @return
     */
    String getTaskName();

    /**
     * 获取定时任务创建时间
     * @return
     */
    LocalDateTime getCreateTime();

    /**
     * 获取定时任务cron表达式
     * @return
     */
    String getCron();

}
