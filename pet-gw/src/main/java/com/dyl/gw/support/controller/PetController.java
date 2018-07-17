package com.dyl.gw.support.controller;

import com.diyiliu.plugin.util.DateUtil;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.dto.PetGpsHis;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import com.dyl.gw.support.jpa.facade.PetGpsHisJpa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Description: PetController
 * Author: DIYILIU
 * Update: 2018-07-17 15:01
 */

@RestController
@RequestMapping("/pet")
@Api(description = "宠物查询接口")
public class PetController {

    @Resource
    private PetGpsCurJpa petGpsCurJpa;

    @Resource
    private PetGpsHisJpa petGpsHisJpa;

    @GetMapping("/find/{id}")
    @ApiOperation(value = "实时信息", notes = "查询宠物当前信息")
    public PetGpsCur findPet(@PathVariable long id) {

        return petGpsCurJpa.findById(id);
    }

    @GetMapping("/track")
    @ApiOperation(value = "轨迹追踪", notes = "查询宠物历史轨迹")
    public List<PetGpsHis> petTrack(@RequestParam long id, @RequestParam String dateTime) {
        String starTime = dateTime.substring(0, 19);
        String endTime = dateTime.substring(22, 41);
        Date sTime = DateUtil.stringToDate(starTime);
        Date eTime = DateUtil.stringToDate(endTime);

        return petGpsHisJpa.findByDeviceIdAndGpsTimeBetween(id, sTime, eTime, Sort.by(Sort.Direction.DESC, "gpsTime"));
    }
}
