package com.example.demo.Component;

import com.example.demo.Schedule.ScheduledTask;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务注册组件
 * 用于定时任务的执行，新增，移出
 */
@Component
public class TaskRegister implements DisposableBean {
    private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    @Autowired
    private TaskScheduler taskScheduler;

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    /**
     * 新增定时任务
     *
     * @param task
     * @param cronExpression
     */
    public void addCronTask(Runnable task, String cronExpression) {
        addCronTask(new CronTask(task, cronExpression));
    }

    /**
     * 新增定时任务
     *
     * @param task          执行的任务
     * @param interval     任务执行间隔
     * @param initialDelay 开始执行任务的初始延迟
     */
    public void addCronTask(Runnable task, long interval, long initialDelay) {
        addCronTask(new FixedDelayTask(task, interval, initialDelay));
    }

    public void addCronTask(CronTask cronTask) {
        if (cronTask != null) {
            Runnable task = cronTask.getRunnable();
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }

            this.scheduledTasks.put(task, scheduleCronTask(cronTask));
        }
    }

    public void addCronTask(FixedDelayTask fixedDelayTask) {
        if (fixedDelayTask != null) {
            Runnable task = fixedDelayTask.getRunnable();
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }

            this.scheduledTasks.put(task, scheduleCronTask(fixedDelayTask));
        }
    }

    public void addFixedDelayTask(FixedDelayTask fixedDelayTask) {
        if (fixedDelayTask != null) {
            Runnable task = fixedDelayTask.getRunnable();
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }

            this.scheduledTasks.put(task, scheduleCronTask(fixedDelayTask));
        }
    }

    /**
     * 移除定时任务
     *
     * @param task
     */
    public boolean removeCronTask(Runnable task) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(task);
        if (scheduledTask != null) {
            scheduledTask.cancel();
            return true;
        }
        return false;
    }

    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }

    public ScheduledTask scheduleCronTask(FixedDelayTask fixedDelayTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(fixedDelayTask.getRunnable(), fixedDelayTask.getInterval());
        return scheduledTask;
    }


    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
