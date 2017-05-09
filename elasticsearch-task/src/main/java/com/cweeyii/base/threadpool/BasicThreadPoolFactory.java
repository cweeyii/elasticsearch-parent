package com.cweeyii.base.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.*;

/**
 * Created by wenyi on 16/11/10.
 * Email:caowenyi@meituan.com
 */
public class BasicThreadPoolFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicThreadPoolFactory.class);

    private BasicThreadPoolFactory() {
    }

    public static ExecutorService newThreadPoolExecutor(String threadPoolName, int threadPoolSize, int queueSize) {
        return buildTaskPoolExecutor(threadPoolName, threadPoolSize, queueSize);
    }

    public static ExecutorService newThreadPoolExecutor(String threadPoolName, int queueSize) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = availableProcessors * 2;
        return buildTaskPoolExecutor(threadPoolName, threadPoolSize, queueSize);
    }


    private static ThreadPoolExecutor buildTaskPoolExecutor(String threadPoolName, int threadPoolSize, int queueSize) {
        LOGGER.info("分配的线程数量" + threadPoolSize);
        return new ThreadPoolExecutor(
                threadPoolSize, threadPoolSize, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                final SecurityManager s = System.getSecurityManager();
                ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                        .getThreadGroup();
                final Thread t = new Thread(group, r, String.format(Locale.ROOT, "%s",
                        threadPoolName), 0);
                t.setDaemon(false);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
