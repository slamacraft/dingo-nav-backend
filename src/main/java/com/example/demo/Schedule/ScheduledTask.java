package com.example.demo.Schedule;

import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务控制类
 */
public final class ScheduledTask {

    public volatile ScheduledFuture<?> future;
    /**
     * 取消定时任务
     * 通过cancel优雅结束线程
     */
    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }
}
