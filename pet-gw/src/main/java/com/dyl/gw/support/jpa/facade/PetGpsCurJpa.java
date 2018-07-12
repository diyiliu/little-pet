package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.PetGpsCur;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: PetGpsCurJpa
 * Author: DIYILIU
 * Update: 2018-07-12 10:04
 */
public interface PetGpsCurJpa extends JpaRepository<PetGpsCur, Long> {

    PetGpsCur findByDeviceId(String deviceId);
}
