package com.dyl.gw.support.task;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.task.ITask;
import com.diyiliu.plugin.util.DateUtil;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * Description: KeepAliveTask
 * Author: DIYILIU
 * Update: 2018-06-27 14:43
 */

@Slf4j
@Component
public class KeepAliveTask implements ITask {
    private final static int MSG_IDLE = 6 * 60 * 1000;

    @Resource
    private ICache onlineCacheProvider;

    @Resource
    private PetGpsCurJpa petGpsCurJpa;

    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 60 * 1000)
    public void execute() {
        Set<Object> keys = onlineCacheProvider.getKeys();
        keys.stream().forEach(e -> {

            MsgPipeline pipeline = (MsgPipeline) onlineCacheProvider.get(e);
            if (System.currentTimeMillis() - pipeline.getTime() > MSG_IDLE ||
                    !pipeline.getContext().channel().isOpen()) {
                log.info("设备离线[{}], 检测时间[{}]", e, DateUtil.dateToString(new Date()));

                onlineCacheProvider.remove(e);
                pipeline.getContext().close();

                // 设备离线
                String device = (String) e;
                PetGpsCur curGps = petGpsCurJpa.findByDevice(device);
                curGps.setStatus(0);
                petGpsCurJpa.save(curGps);
            }
        });

    }
}
