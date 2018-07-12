package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.PetGps;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: PetGpsJpa
 * Author: DIYILIU
 * Update: 2018-07-12 10:04
 */
public interface PetGpsJpa extends JpaRepository<PetGps, Long> {

    PetGps findByDeviceId(String deviceId);
}
