package kr.movements.smv2.repository;

import kr.movements.smv2.dto.SiteCertification;
import kr.movements.smv2.entity.SiteManagerCertificationEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SiteManagerCertificationRepository extends JpaRepository<SiteManagerCertificationEntity, Long> {

    Optional<SiteManagerCertificationEntity> findBySiteIdAndHasDeleted(SiteManagerEntity siteId, Boolean hasDeleted);

    @Query(value = "SELECT\n" +
            "site_certification_pw as siteCertificationPw,\n" +
            "modified_date as modifiedDate \n" +
            "FROM\n" +
            "site_manager_certification smc\n" +
            "WHERE\n" +
            "site_id = :endSiteId", nativeQuery = true)
    SiteCertification findCertificationPw(@Param("endSiteId") Long endSiteId);

    @Query(value = "select modified_date from site_manager_certification where site_id = :siteId", nativeQuery = true)
    LocalDateTime qrCreateTime(@Param("siteId") Long id);

    @Query(value = "select site_certification_pw from site_manager_certification where site_id = :siteId", nativeQuery = true)
    String qrPwd(@Param("siteId") Long id);

    @Modifying
    @Query(value = "UPDATE site_manager_certification\n" +
            "SET \n" +
            "\tsite_certification_pw = :siteCertificationPw,\n" +
            "\tmodified_date = :modifiedDate\n" +
            "WHERE site_id IN (\n" +
            "SELECT id FROM site_manager\n" +
            "WHERE user_id = :userId\n" +
            ")", nativeQuery = true)
    void siteManagerCertificationUpdate(@Param("siteCertificationPw") String siteCertificationPw, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("userId") Long userId);
}
