package com.dyl.gw.support.jpa.facade;

        import com.dyl.gw.support.jpa.dto.PetGpsCur;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Query;

/**
 * Description: PetGpsCurJpa
 * Author: DIYILIU
 * Update: 2018-07-12 10:04
 */
public interface PetGpsCurJpa extends JpaRepository<PetGpsCur, Long> {

    PetGpsCur findByDevice(String device);

    PetGpsCur findById(long id);

    @Query("select t from PetGpsCur t where t.id =?1 or t.device = ?2 or t.petInfo.name = ?3")
    PetGpsCur findByIdOrDeviceOrName(long search1, String search2, String search3);
}
