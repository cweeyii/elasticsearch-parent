package com.cweeyii.base.util;

import com.cweeyii.base.threadpool.BasicThreadPoolFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.System.currentTimeMillis;

public class ScanDomainUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanDomainUtil.class);

    public <T> void scanDomain(Object bean, String scanMethodName, String idMethodName, long begId, int pageSize, Class<T> objClass, ScanFunction<T> scanFunction) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        scanDomain(bean, scanMethodName, idMethodName, begId, pageSize, objClass, 4, 50, scanFunction);
    }

    public <T> void scanDomain(Object bean, String scanMethodName, String idMethodName, long begId, int pageSize, Class<T> objClass, int poolSize, int queueSize, ScanFunction<T> scanFunction) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        scanDomain(bean, scanMethodName, idMethodName, begId, pageSize, 100, objClass, poolSize, queueSize, scanFunction);
    }

    public <T> void scanDomain(Object bean, String scanMethodName, String idMethodName, long begId, int pageSize, int batchSize, Class<T> objClass, int poolSize, int queueSize, ScanFunction<T> scanFunction) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {
        ExecutorService executorService = BasicThreadPoolFactory.newThreadPoolExecutor(bean.getClass().getSimpleName(), poolSize, queueSize);
        Long startTime = currentTimeMillis();
        try {
            if (StringUtils.isBlank(scanMethodName) || StringUtils.isBlank(idMethodName)) {
                throw new NoSuchMethodException("scanMethod和idMethod为必填参数");
            }

            Method idMethod = objClass.getMethod(idMethodName);
            Method scanMethod = bean.getClass().getMethod(scanMethodName, Long.class, int.class);
            while (!Thread.interrupted()) {
                Long begTime = currentTimeMillis();
                List<T> objectList = (List<T>) scanMethod.invoke(bean, begId, pageSize);
                if (CollectionUtils.isEmpty(objectList)) {
                    break;
                }
                List<List<T>> lists = Lists.partition(objectList, batchSize);
                for (List<T> list : lists) {
                    executorService.submit(new ScanWorker<>(list, scanFunction));
                }
                T lastObj = objectList.get(objectList.size() - 1);
                Long endId = (Long) idMethod.invoke(lastObj);
                LOGGER.info("id from {} to {} cost time {} seconds", new Object[]{begId, endId, (currentTimeMillis() - begTime) / 1000});
                begId = endId;
            }
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            LOGGER.info("{} finished cost time {} seconds", scanMethodName, (currentTimeMillis() - startTime) / 1000);
        }
    }

    private class ScanWorker<TObj> implements Runnable {
        private List<TObj> mObjList;
        private ScanFunction<TObj> mScanFunction;

        private ScanWorker(List<TObj> objList, ScanFunction<TObj> scanFunction) {
            this.mObjList = objList;
            this.mScanFunction = scanFunction;
        }

        @Override
        public void run() {
            mScanFunction.doWorker(mObjList);
        }
    }

    public interface ScanFunction<T> {
        void doWorker(List<T> objList);
    }
}
