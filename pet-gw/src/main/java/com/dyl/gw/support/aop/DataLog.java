package com.dyl.gw.support.aop;

import com.dyl.gw.support.jpa.dto.RawData;
import com.dyl.gw.support.jpa.facade.RawDataJpa;
import com.dyl.gw.support.model.MsgBody;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Description: DataLog
 * Author: DIYILIU
 * Update: 2018-07-11 23:14
 */

@Aspect
@Component
public class DataLog {

    @Resource
    private RawDataJpa rawDataJpa;

    @After("execution(* com.dyl.gw.protocol.PetDataProcess.parse(..))")
    public void doAfter(JoinPoint point){
        MsgBody msgBody = (MsgBody) point.getArgs()[0];

        // 记录原始指令
        RawData rawData = new RawData();
        rawData.setDevice(msgBody.getDevice());
        rawData.setCmd(msgBody.getCmd());
        rawData.setData(msgBody.getText());
        rawData.setFlow("上行");
        rawData.setDatetime(new Date());

        rawDataJpa.save(rawData);
    }
}
