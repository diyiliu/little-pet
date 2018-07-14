package com.dyl.gw.support.controller;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.SendMsg;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import com.dyl.gw.support.task.MsgSenderTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: SenderController
 * Author: DIYILIU
 * Update: 2018-07-14 07:15
 */

@RestController
@RequestMapping("/setup")
@Api(description = "设备指令下发接口")
public class SenderController {

    @Resource
    private ICache onlineCacheProvider;

    @Resource
    private PetGpsCurJpa petGpsCurJpa;

    @PostMapping
    @ApiOperation(value = "参数设置", notes = "原始指令参数设置")
    public String setup(@RequestParam long deviceId, @RequestParam String cmd, @RequestParam String value,
                        @RequestParam long rowId, HttpServletResponse response) {

        PetGpsCur petGpsCur = petGpsCurJpa.findById(deviceId);
        if (petGpsCur == null) {

            response.setStatus(500);
            return "设备不存在。";
        }

        String device = petGpsCur.getDevice();
        if (!onlineCacheProvider.containsKey(device)) {

            response.setStatus(500);
            return "设备离线。";
        }

        SendMsg msg = new SendMsg();
        msg.setDevice(device);
        msg.setCmd(cmd);
        msg.setContent(value.getBytes());
        MsgSenderTask.send(msg);

        return "OK";
    }

    private final AtomicInteger serialNo = new AtomicInteger(0);

    /**
     * 自增序列
     *
     * @return
     */
    public int getSerial(){
        int serial = serialNo.incrementAndGet();
        if (serial > 65535){
            serialNo.set(0);

            return 1;
        }

        return serial;
    }
}
