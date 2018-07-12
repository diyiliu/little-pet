package com.dyl.gw.support.config;

import com.diyiliu.plugin.cache.ICache;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * Description: GwQuartz
 * Author: DIYILIU
 * Update: 2018-07-12 10:02
 */

@Configuration
@EnableScheduling
public class GwQuartz {

    @Resource
    private ICache petCacheProvider;

    /**
     * 刷新宠物列表
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 3 * 1000)
    public void executeVehicleTask() {

    }
}
