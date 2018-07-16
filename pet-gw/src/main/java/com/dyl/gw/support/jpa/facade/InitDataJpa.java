package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.InitData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Description: InitDataJpa
 * Author: DIYILIU
 * Update: 2018-07-16 09:28
 */
public interface InitDataJpa extends JpaRepository<InitData, Long> {

    List<InitData> findByDeviceIdAndDatetimeBetween(long deviceId, Date start, Date end);
}
