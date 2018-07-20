package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.PetInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: PetInfoJpa
 * Author: DIYILIU
 * Update: 2018-07-18 16:23
 */
public interface PetInfoJpa extends JpaRepository<PetInfo, Long> {

}
