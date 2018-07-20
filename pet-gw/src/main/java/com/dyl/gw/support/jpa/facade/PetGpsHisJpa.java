package com.dyl.gw.support.jpa.facade;

import com.dyl.gw.support.jpa.dto.PetGpsHis;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Description: PetGpsHisJpa
 * Author: DIYILIU
 * Update: 2018-07-12 22:47
 */
public interface PetGpsHisJpa extends JpaRepository<PetGpsHis, Long>, JpaSpecificationExecutor<PetGpsHis> {

    List<PetGpsHis> findByDeviceIdAndGpsTimeBetween(long id, Date start, Date end, Sort sort);
}
