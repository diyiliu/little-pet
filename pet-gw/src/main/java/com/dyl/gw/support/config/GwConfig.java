package com.dyl.gw.support.config;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.cache.ram.RamCacheProvider;
import com.diyiliu.plugin.util.SpringUtil;
import com.dyl.gw.netty.server.PetServer;
import com.dyl.gw.support.util.GdLocationUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * Description: GwConfig
 * Author: DIYILIU
 * Update: 2018-06-27 14:13
 */

@Configuration
@PropertySource(value = {"classpath:config/config.properties"})
public class GwConfig {

    @Resource
    private Environment environment;

    @Bean
    public GdLocationUtil locationUtil(){
        GdLocationUtil locationUtil = new GdLocationUtil();
        locationUtil.setAmapUrl(environment.getProperty("gd.url"));
        locationUtil.setUserKey(environment.getProperty("gd.key"));

        return locationUtil;
    }

    @Bean
    public PetServer petServer() {
        PetServer gwServer = new PetServer();
        gwServer.setPort(environment.getProperty("gw.port", Integer.class));
        gwServer.init();

        return gwServer;
    }

    /**
     * spring 工具类
     *
     * @return
     */
    @Bean
    public SpringUtil springUtil() {

        return new SpringUtil();
    }

    /**
     * 设备在线
     *
     * @return
     */
    @Bean
    public ICache onlineCacheProvider() {

        return new RamCacheProvider();
    }

    /**
     * 宠物缓存
     *
     * @return
     */
    @Bean
    public ICache petCacheProvider() {

        return new RamCacheProvider();
    }
}
