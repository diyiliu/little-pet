package com.dyl.gw.support.task;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.task.ITask;
import com.diyiliu.plugin.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: KeepAliveTask
 * Author: DIYILIU
 * Update: 2018-06-27 14:43
 */

@Slf4j
public class KeepAliveTask implements ITask, Runnable {
    private final static int MSG_IDLE = 10 * 60 * 1000;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private ICache onlineCacheProvider;

    @Override
    public void execute() {
        executorService.scheduleWithFixedDelay(this, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

        Set<Object> keys = onlineCacheProvider.getKeys();
        keys.stream().forEach(e -> {

            MsgPipeline pipeline = (MsgPipeline) onlineCacheProvider.get(e);
            if (System.currentTimeMillis() - pipeline.getTime() > MSG_IDLE ||
                    !pipeline.getContext().channel().isOpen()){

                onlineCacheProvider.remove(e);
                log.info("设备离线[{}], 检测时间[{}]", e, DateUtil.dateToString(new Date()));
            }
        });
    }

    public void setOnlineCacheProvider(ICache onlineCacheProvider) {
        this.onlineCacheProvider = onlineCacheProvider;
    }
}
