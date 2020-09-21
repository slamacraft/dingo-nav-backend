package com.dingdo.component.stopwatch;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 秒表任务类
 * 本类是一个秒表任务的基类，包含了秒表任务的基本参数
 * {@code getTask}方法可以循环式地获取下一次调度任务
 * 可以通过{@code stop}和{@code toContinue}方法设置任务的运行状态
 * @author slamacraft
 * @date 2020/9/16 10:27
 * @since JDK 1.8
 */
public class StopWatchFuture {

    protected String id;
    // 任务开始时间，单位秒
    protected Long startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    // 当前秒表任务执行的任务序号
    protected int taskIndex;
    // 秒表任务列表
    protected StopWatchTask[] taskList;
    // 剩余的执行次数：-1为无限次
    protected Long count;
    // 暂停的时间点，单位秒
    protected long stopTimePoint;
    // 是否暂停
    protected boolean stopFlag = false;

    /**
     * 秒表任务构造方法
     *
     * @param id       秒表任务唯一标识字符串
     * @param taskList 秒表任务task数组
     * @param count    秒表任务执行的次数，task全部执行完算一次
     */
    public StopWatchFuture(String id, StopWatchTask[] taskList, Long count) {
        this.id = id;
        this.taskList = taskList;
        this.count = count;
        startTime += taskList[0].getWaitTime();

    }

    /**
     * 秒表任务构造方法
     *
     * @param id       秒表任务唯一标识字符串
     * @param taskList 秒表任务task列表
     * @param count    秒表任务执行的次数，task全部执行完算一次
     */
    public StopWatchFuture(String id, List<StopWatchTask> taskList, Long count) {
        this(id, (StopWatchTask[]) taskList.toArray(), count);
    }

    /**
     * 无参构造方法，为子类初始化使用
     */
    protected StopWatchFuture() {
    }


    /**
     * 暂停本秒表任务，并记录下暂停时间戳
     */
    public void stop() {
        this.stopTimePoint = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        stopFlag = true;
    }

    /**
     * 继续本秒表任务，并在开始时间上加上暂停的时间
     */
    public void toContinue() {
        long stopTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) - stopTimePoint;
        startTime += stopTime;
        stopFlag = true;
    }

    /**
     * 获取本秒表任务id
     * @return  id
     */
    public String getId() {
        return this.id;
    }


    /**
     * 获取下次执行的task
     * @return  StopWatchTask实例
     */
    public StopWatchTask getTask() {
        StopWatchTask stopWatchTask = taskList[taskIndex];
        taskIndex++;
        if (taskIndex >= taskList.length) {
            taskIndex = 0;
            if (count > 0) {
                count--;
            }
        }
        startTime += taskList[taskIndex].getWaitTime();
        return stopWatchTask;
    }


    /**
     * 获取下次执行的时间戳
     * @return
     */
    public Long getStartTime() {
        return startTime;
    }


    /**
     * 获取剩余的执行次数
     * @return
     */
    public Long getCount() {
        return count;
    }

    public boolean isStop() {
        return stopFlag;
    }
}
