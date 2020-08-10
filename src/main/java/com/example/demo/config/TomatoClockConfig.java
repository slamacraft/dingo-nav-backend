package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class TomatoClockConfig {

    /**
     * 注册一个番茄钟线程池bean
     * @return
     */
    @Bean
    public ThreadPoolExecutor tomatoClockPool(){
        return new ThreadPoolExecutor(
                1,  // 核心线程池大小（没加几个群，用不了多少核心线程）
                10,  // 最大线程池大小
                1, TimeUnit.MINUTES,    // 阻塞队列的生存时间
                new ArrayBlockingQueue<Runnable>(15),   // 阻塞队列长度
                new ThreadPoolExecutor.DiscardPolicy()    // 拒绝策略：什么也不做
        );
    }

}
