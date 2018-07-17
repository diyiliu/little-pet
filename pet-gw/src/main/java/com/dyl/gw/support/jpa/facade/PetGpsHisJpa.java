package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.PetGpsHis;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Description: PetGpsHisJpa
 * Author: DIYILIU
 * Update: 2018-07-12 22:47
 */
public interface PetGpsHisJpa extends JpaRepository<PetGpsHis, Long> {

    List<PetGpsHis> findByDeviceIdAndGpsTimeBetween(long id, Date start, Date end, Sort sort);
}
