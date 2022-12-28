package com.dingo.component.stopwatch;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 单线程非阻塞的多秒表任务执行器
 *
 * @author slamacraft
 * @date 2020/9/16 14:01
 * @since JDK 1.8
 */
public class StopWatchExecutor implements Runnable {

    // 为执行器提供数据的处理器
    private StopWatchHandler handler;

    // 执行器是否在运行
    private boolean runningFlag = true;


    /**
     * 默认的访问限制，使其只能与StopWatchRegister配合使用，不能单独成为一个实例
     * 需要传入一个StopWatchHandler对象来获取需要运行的对象
     * @param handler   秒表任务处理器实例
     */
    StopWatchExecutor(StopWatchHandler handler) {
        this.handler = handler;
    }


    /**
     * 秒表任务的执行方法，该方法会进行异步调用
     * 该方法运行期间会不断低从处理器的秒表任务最小堆中获取堆顶元素的run方法进行执行，
     * 如果目前时间还没到达堆顶元素任务执行的开始时间，则在这段时间中进行线程休眠，
     * 每当处理器的堆进行变动后，都会唤醒该线程，并重新计算休眠时间
     */
    @Override
    public void run() {
        while (runningFlag) {
            StopWatchFuture stopWatchFuture = handler.get();
            if (stopWatchFuture == null) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }

            Long startTime = stopWatchFuture.getStartTime();
            long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            long time = startTime - now;
            if (time > 0) {
                synchronized (this) {
                    try {
                        this.wait(time * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }

            StopWatchFuture future = handler.remove();
            StopWatchTask task = future.getTask();
            task.execute();

            handler.add(future);
        }
    }


    /**
     * 销毁方法
     */
    public void destroy(){
        this.runningFlag = false;
    }
}
