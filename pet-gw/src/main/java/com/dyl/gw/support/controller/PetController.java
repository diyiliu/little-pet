package com.dyl.gw.support.controller;

import com.diyiliu.plugin.util.DateUtil;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.dto.PetGpsHis;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import com.dyl.gw.support.jpa.facade.PetGpsHisJpa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.regex.Pattern;

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


    @PostMapping("/petList")
    @ApiOperation(value = "实时信息", notes = "分页查询宠物信息")
    public Map petList(@RequestParam int page, @RequestParam int size,
                       @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "systemTime"));
        Page<PetGpsCur> petInfoPage = petGpsCurJpa.findAll(pageable);

        Map respMap = new HashMap();
        respMap.put("data", petInfoPage.getContent());
        respMap.put("total", petInfoPage.getTotalElements());

        return respMap;
    }


    @GetMapping("/find/{id}")
    @ApiOperation(value = "实时信息", notes = "查询宠物当前信息")
    public PetGpsCur findPet(@PathVariable long id) {

        return petGpsCurJpa.findById(id);
    }

/*
    @GetMapping("/track")
    @ApiOperation(value = "轨迹追踪", notes = "查询宠物历史轨迹(时间格式:2018-06-16 16:05:43 - 2018-07-17 16:05:43)")
    public List<PetGpsHis> petTrack(@RequestParam long id, @RequestParam String dateTime) {
        String starTime = dateTime.substring(0, 19);
        String endTime = dateTime.substring(22, 41);
        Date sTime = DateUtil.stringToDate(starTime);
        Date eTime = DateUtil.stringToDate(endTime);

        return petGpsHisJpa.findByDeviceIdAndGpsTimeBetween(id, sTime, eTime, Sort.by(Sort.Direction.DESC, "gpsTime"));
    }
*/

    @GetMapping("/track")
    @ApiOperation(value = "轨迹追踪", notes = "查询宠物历史轨迹(时间格式:2018-06-16 16:05:43 - 2018-07-17 16:05:43)")
    public List<PetGpsHis> petTrack(@RequestParam String search, @RequestParam String dateTime) {
        String starTime = dateTime.substring(0, 19);
        String endTime = dateTime.substring(22, 41);
        Date sTime = DateUtil.stringToDate(starTime);
        Date eTime = DateUtil.stringToDate(endTime);

        long id = 0;
        if (isNumeric(search)) {
            id = Long.valueOf(search);
        }

        PetGpsCur gpsCur = petGpsCurJpa.findByIdOrDeviceOrName(id, search, search);
        if (gpsCur == null) {

            return null;
        }

        return petGpsHisJpa.findAll(
                (Root<PetGpsHis> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                    Path<Date> createTimeExp = root.get("gpsTime");
                    Path<String> deviceIdExp = root.get("deviceId");
                    Path<Integer> locationExp = root.get("location");

                    List<Predicate> list = new ArrayList();
                    list.add(cb.equal(deviceIdExp, gpsCur.getId()));
                    list.add(cb.between(createTimeExp, sTime, eTime));
                    list.add(cb.greaterThan(locationExp, 0));

                    Predicate[] predicates = list.toArray(new Predicate[]{});
                    return cb.and(predicates);
                });
    }


    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
