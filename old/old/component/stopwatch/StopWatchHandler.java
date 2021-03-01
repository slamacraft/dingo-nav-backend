package com.example.old.component.stopwatch;

import org.apache.commons.collections.buffer.PriorityBuffer;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.util.Comparator;

/**
 * 单线程非阻塞多任务式秒表任务处理器
 * @author slamacraft
 * @date 2020/9/16 10:27
 * @since JDK 1.8
 * @see StopWatchExecutor
 */
public class StopWatchHandler implements Destroyable {

    /**
     * 正在等待运行的秒表任务最小堆
     */
    volatile private PriorityBuffer heap = new PriorityBuffer(Comparator.comparing(StopWatchFuture::getStartTime));

    /**
     * 秒表任务的执行器
     */
    private final StopWatchExecutor executor;


    /**
     * 构造方法
     * 会在初始化时创建一个新执行器, 并将其创建为一个新的线程
     */
    public StopWatchHandler() {
        executor = new StopWatchExecutor(this);
        Thread thread = new Thread(executor);
        thread.start();
    }


    /**
     * 获取秒表任务最小堆顶部的任务实例
     * 当堆为空时，将会返回null
     * @return  秒表任务实例
     */
    public StopWatchFuture get() {
        if (heap.isEmpty()) {
            return null;
        }
        return (StopWatchFuture) heap.get();
    }


    /**
     * 新增秒表任务
     * 当新增的秒表任务执行次数不等于0时，便会将该任务插入到最小堆中，
     * 并使执行器接触休眠，使其执行当前任务或重新计算休眠时间，并返回true，
     * 如果新增的秒表任务执行次数等于0，则返回false
     * 如果执行次数等于 -1，则表示无限次执行
     * @param future    新增的秒表任务
     * @return  是否新增成功
     */
    public boolean add(StopWatchFuture future) {
        if (future.getCount() == 0) {
            return false;
        }
        heap.add(future);
        
        synchronized (executor){
            executor.notify();
        }
        return true;
    }

    /**
     * 移除秒表任务
     * 将传入的秒表任务从最小堆中移除，并唤醒休眠中的执行器
     * 如果秒表任务在最小堆中存在，则返回true，
     * 否则返回false
     * @param future    秒表任务实例
     * @return  是否移除成功
     */
    public boolean remove(StopWatchFuture future) {
        boolean result = heap.remove(future);
        synchronized (executor){
            executor.notify();
        }
        return result;
    }


    /**
     * 移除最小堆的堆顶秒表任务并返回
     *  如果堆的大小为0，则返回null
     * @return  堆顶的秒表任务
     */
    public StopWatchFuture remove() {
        if(heap.isEmpty()){
            return null;
        }
        StopWatchFuture result = (StopWatchFuture) heap.remove();
        synchronized (executor){
            executor.notify();
        }
        return result;
    }


    /**
     * 终止秒表任务执行器
     * @throws DestroyFailedException   终止秒表任务执行器失败
     */
    @Override
    public void destroy() throws DestroyFailedException {
        executor.destroy();
        synchronized (executor){
            executor.notify();
        }
    }
}
