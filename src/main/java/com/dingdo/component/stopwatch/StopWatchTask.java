package com.dingdo.component.stopwatch;

/**
 * 秒表任务具体的执行方法类
 * 能够设置任务开始的等待延迟
 *
 * @author slamacraft
 * @date: 2020/9/17 9:18
 * @since JDK 1.8
 */
public class StopWatchTask{

    private final Runnable task;

    private final long waitTime;


    /**
     * 构造器
     * @param task  Runnable
     * @param waitTime      延迟等待时间
     */
    public StopWatchTask(Runnable task, long waitTime) {
        this.task = task;
        this.waitTime = waitTime;
    }

    /**
     * 执行秒表任务
     */
    public void execute() {
        task.run();
    }

    /**
     * 获取等待时间
     * @return  等待时间
     */
    public long getWaitTime() {
        return waitTime;
    }
}
