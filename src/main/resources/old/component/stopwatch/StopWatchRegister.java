package com.dingdo.component.stopwatch;

import org.springframework.stereotype.Component;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.util.HashMap;
import java.util.Map;

/**
 * 秒表任务注册组件
 * @author slamacraft
 * @date 2020/9/16 10:26
 * @since JDK 1.8
 * @see StopWatchHandler
 */
@Component
public class StopWatchRegister implements Destroyable {

    /**
     * 存放秒表任务的map
     * key：id，value：StopWatchFuture
     * 通过秒表任务设定的id确定唯一性
     */
    private Map<String, StopWatchFuture> futureMap = new HashMap<>();

    /**
     * 秒表任务处理器
     * 负责对从本注册器实例注册的秒表任务进行管理
     */
    private StopWatchHandler handler = new StopWatchHandler();

    /**
     * 通过id获取注册过的秒表任务
     * 即使秒表任务已经结束，同样也可以在本方法中得到
     * @param id    秒表任务id
     * @return  查询到的秒表任务
     */
    public StopWatchFuture getFuture(String id) {
        return futureMap.get(id);
    }


    /**
     * 新增秒表任务
     * 通过秒表任务自带的id判断唯一性
     * 如果id相同，后注册的秒表任务会覆盖掉先注册的秒表任务
     * @param future    秒表任务实例
     */
    public void addFuture(StopWatchFuture future) {
        StopWatchFuture runningFuture = getFuture(future.getId());
        if(runningFuture != null){
            handler.remove(runningFuture);
        }

        futureMap.put(future.getId(), future);
        handler.add(future);
    }


    /**
     * 暂停秒表任务
     * 通过输入秒表任务的id来暂停秒表任务
     * 如果秒表任务的id在本实例中注册过且正在运行，则返回true
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否暂停成功
     */
    public boolean stopFuture(String id) {
        StopWatchFuture future = futureMap.get(id);
        if(future == null){
            return false;
        }
        future.stop();
        return handler.remove(future);
    }


    /**
     * 继续暂停的秒表任务
     * 通过输入秒表任务的id来继续秒表任务
     * 如果输入的id在本实例中注册过且正在暂停，则返回true，
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否成功继续
     */
    public boolean continueFuture(String id) {
        StopWatchFuture future = futureMap.get(id);
        if(future == null || future.isStop()){
            return false;
        }
        future.toContinue();
        return handler.add(future);
    }


    /**
     * 移除秒表任务
     * 通过输入的id移除秒表任务，会将秒表任务从本实例中移除，
     * 并且立即中断正在或准备执行的秒表任务
     * 如果输入的id在本实例中注册过，则返回true，
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否移除成功
     */
    public boolean removeFuture(String id) {
        StopWatchFuture toRemoveFuture = futureMap.remove(id);
        return handler.remove(toRemoveFuture);
    }


    /**
     * 秒表注册器销毁
     * @throws DestroyFailedException 销毁失败异常
     */
    @Override
    public void destroy() throws DestroyFailedException {
        handler.destroy();
    }
}
