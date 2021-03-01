package com.example.old.component.schedule.model.interfacor;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 14:00
 * @since JDK 1.8
 */
public interface IScheduledRunnable extends Runnable {

    /**
     * 需要重写equals方法
     * @return
     */
    boolean equals(Object obj);

    /**
     * 需要重写hashCode方法
     * @return
     */
    int hashCode();

}
