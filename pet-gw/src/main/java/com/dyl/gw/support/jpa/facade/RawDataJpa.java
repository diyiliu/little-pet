package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: RawDataJpa
 * Author: DIYILIU
 * Update: 2018-07-11 22:23
 */
public interface RawDataJpa extends JpaRepository<RawData, Long> {

}
