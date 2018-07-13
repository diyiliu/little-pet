package com.dyl.gw.support.task;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.model.SendMsg;
import com.diyiliu.plugin.task.ITask;
import com.dyl.gw.support.jpa.dto.RawData;
import com.dyl.gw.support.jpa.facade.RawDataJpa;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: MsgSenderTask
 * Author: DIYILIU
 * Update: 2018-06-26 16:01
 */

@Slf4j
public class MsgSenderTask implements ITask, Runnable {
    private final static Queue<SendMsg> msgPool = new ConcurrentLinkedQueue();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Resource
    private ICache onlineCacheProvider;

    @Resource
    private RawDataJpa rawDataJpa;

    @Override
    public void execute() {
        executorService.scheduleAtFixedRate(this, 3, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        while (!msgPool.isEmpty()) {
            SendMsg msg = msgPool.poll();

            String device = msg.getDevice();
            String cmd = msg.getCmd();
            byte[] content = msg.getContent();

            if (onlineCacheProvider.containsKey(device)) {
                log.info("下行, 设备[{}], 指令[{}],  内容: {}", device, cmd, new String(content));

                MsgPipeline pipeline = (MsgPipeline) onlineCacheProvider.get(device);
                pipeline.getContext().writeAndFlush(Unpooled.copiedBuffer(content));

                // 记录原始指令
                RawData rawData = new RawData();
                rawData.setDevice(device);
                rawData.setCmd(cmd);
                rawData.setData(new String(content));
                rawData.setFlow("下行");
                rawData.setDatetime(new Date());

                rawDataJpa.save(rawData);
            } else {
                log.warn("设备[{}]离线, 指令下发失败!", device);
            }
        }
    }


    public static void send(SendMsg msg) {
        msgPool.add(msg);
    }
}
