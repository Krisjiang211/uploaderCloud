package com.xiangyueEducation.uploaderCloud.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TreadPoolConfig {

    @Bean("fileHandleThreadPool")
    public Executor FileHandleThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：线程池维持的最小线程数
        executor.setCorePoolSize(10);
        // 最大线程数：线程池可容纳的最大线程数
        executor.setMaxPoolSize(200);
        // 队列容量：当所有核心线程都在忙时，任务会进入队列等待执行
        executor.setQueueCapacity(9999);
        // 线程活跃时间：当线程数超过核心线程数时(CorePoolSize)，多余的线程空闲的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 线程名的前缀，便于调试和监控
        executor.setThreadNamePrefix("Thread-");
        // 拒绝策略：当线程池满了后如何处理新的任务(new ThreadPoolExecutor.CallerRunsPolicy()即使线程池满了,但是还是会让这个线程执行,确保一定执行)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }

}
