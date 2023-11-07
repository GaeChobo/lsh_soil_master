package kr.movements.smv2.repository;

import kr.movements.smv2.dto.ContractInfo;
import kr.movements.smv2.dto.ContractList;
import kr.movements.smv2.entity.ContractEntity;

import kr.movements.smv2.entity.SiteManagerEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>, ContractRepositoryCustom {

    @Query(value = "select \n" +
            "\tid as contractId\n" +
            "from contract c\n" +
            "where has_deleted = false and start_site_id = :startSiteId and end_site_id = :endSiteId ", nativeQuery = true)
    Long findByContractId(@Param("startSiteId") Long startSiteId, @Param("endSiteId") Long endSiteId);



    @Query(value = "SELECT\n" +
            "c.id as contractId,\n" +
            "ui.user_name as creator,\n" +
            "c.created_date as createdDate,\n" +
            "s1.site_name as startSiteName,\n" +
            "s2.site_name as endSiteName\n" +
            "FROM contract c\n" +
            "INNER JOIN site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN site_manager s2 ON c.end_site_id = s2.id\n" +
            "INNER JOIN user_info ui ON CAST(c.created_by AS BIGINT) = ui.id\n" +
            "WHERE c.has_deleted = false" , nativeQuery = true)
    Page<ContractList> adminContractList(Pageable pageable);

    @Query(value = "SELECT\n" +
            "c.id as contractId,\n" +
            "ui.user_name as creator,\n" +
            "c.created_date as createdDate,\n" +
            "s1.site_name as startSiteName,\n" +
            "s2.site_name as endSiteName\n" +
            "FROM contract c\n" +
            "INNER JOIN site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN site_manager s2 ON c.end_site_id = s2.id\n" +
            "INNER JOIN user_info ui ON CAST(c.created_by AS BIGINT) = ui.id\n" +
            "WHERE c.has_deleted = false\n" +
            "AND s1.site_name LIKE %:startSiteName%", nativeQuery = true)
    Page<ContractList> adminContractOfStartSiteNameList(@Param("startSiteName") String startSiteName, Pageable pageable);


    @Query(value = "SELECT\n" +
            "c.id as contractId,\n" +
            "ui.user_name as creator,\n" +
            "c.created_date as createdDate,\n" +
            "s1.site_name as startSiteName,\n" +
            "s2.site_name as endSiteName\n" +
            "FROM contract c\n" +
            "INNER JOIN site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN site_manager s2 ON c.end_site_id = s2.id\n" +
            "INNER JOIN user_info ui ON CAST(c.created_by AS BIGINT) = ui.id\n" +
            "WHERE c.has_deleted = false\n" +
            "AND s2.site_name LIKE %:endSiteName%", nativeQuery = true)
    Page<ContractList> adminContractOfEndSiteNameList(@Param("endSiteName") String endSiteName, Pageable pageable);


    @Modifying
    @Query(value = "update contract set created_by = :userName where id = :contractId and has_deleted = false", nativeQuery = true)
    void contractCreatorUpdate(@Param("userName") String userName, @Param("contractId") Long contractId);

    @Modifying
    @Query(value = "update contract set has_deleted = true where id = :contractId" , nativeQuery = true)
    void contractDelete(@Param("contractId") Long contractId);

    @Query(value = "SELECT \n" +
            "\ts1.site_name as startSiteName, \n" +
            "\ts2.site_name as endSiteName,\n" +
            "\tc.created_by as creator,\n" +
            "\tc.created_date as createDate\n" +
            "FROM contract c\n" +
            "INNER JOIN site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN site_manager s2 ON c.end_site_id = s2.id\n" +
            "WHERE c.id = :contractId and c.has_deleted = false" , nativeQuery = true)
    ContractInfo contractInfo(@Param("contractId") Long contractId);

    @Query(value = "select count(*) from contract\n" +
            "where start_site_id = :startSiteId and end_site_id = :endSiteId and has_deleted = false" , nativeQuery = true)
    int contractExisting(@Param("startSiteId") Long startSiteId, @Param("endSiteId") Long endSiteId);

    @Query(value = "SELECT\n" +
            "    c.id AS contractId,\n" +
            "    ui.user_name AS creator,\n" +
            "    c.created_date AS createdDate,\n" +
            "    s1.site_name AS startSiteName,\n" +
            "    s2.site_name AS endSiteName\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN\n" +
            "    site_manager s2 ON c.end_site_id = s2.id\n" +
            "INNER JOIN\n" +
            "    user_info ui ON CAST(c.created_by AS BIGINT) = ui.id\n" +
            "WHERE\n" +
            "    c.has_deleted = false\n" +
            "    AND c.start_site_id = :siteId\n" +
            "    AND s2.site_name LIKE %:endSiteName%", nativeQuery = true)
    Page<ContractList> contractOfStartSiteList(@Param("siteId") Long siteId, @Param("endSiteName") String startSiteName, Pageable pageable);

    @Query(value = "SELECT\n" +
            "    c.id AS contractId,\n" +
            "    ui.user_name AS creator,\n" +
            "    c.created_date AS createdDate,\n" +
            "    s1.site_name AS startSiteName,\n" +
            "    s2.site_name AS endSiteName\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager s1 ON c.start_site_id = s1.id\n" +
            "INNER JOIN\n" +
            "    site_manager s2 ON c.end_site_id = s2.id\n" +
            "INNER JOIN\n" +
            "    user_info ui ON CAST(c.created_by AS BIGINT) = ui.id\n" +
            "WHERE\n" +
            "    c.has_deleted = false\n" +
            "    AND c.end_site_id = :siteId\n" +
            "    AND s1.site_name LIKE %:startSiteName%", nativeQuery = true)
    Page<ContractList> contractOfEndSiteList(@Param("siteId") Long siteId, @Param("startSiteName") String endSiteName, Pageable pageable);

    @Query(value = "select\n" +
            "\tend_site_id\n" +
            "from contract\n" +
            "where id = :contractId and has_deleted = false" , nativeQuery = true)
    Long findByEndSiteId(@Param("contractId") Long contractID);

    Optional<ContractEntity> findByStartSiteIdAndEndSiteId(SiteManagerEntity startSiteId, SiteManagerEntity endSiteId);
    List<ContractEntity> findAllByStartSiteIdAndHasDeleted(SiteManagerEntity startSiteId, Boolean hasDeleted);
    List<ContractEntity> findAllByStartSiteIdAndHasDeletedAndCreatedDateBetween(SiteManagerEntity startSiteId, Boolean hasDeleted, LocalDateTime startDate, LocalDateTime endDate);
    List<ContractEntity> findAllByEndSiteIdAndHasDeleted(SiteManagerEntity endSiteId, Boolean hasDeleted);
    List<ContractEntity> findAllByEndSiteIdAndHasDeletedAndCreatedDateBetween(SiteManagerEntity endSiteId, Boolean hasDeleted, LocalDateTime startDate, LocalDateTime endDate);
    Long countByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
//    Long countByStartSiteIdAndHasDeleted(SiteManagerEntity startSiteId, boolean hasDeleted);
    Long countByStartSiteIdAndHasDeletedAndCreatedDateBetween(SiteManagerEntity startSiteId, boolean hasDeleted, LocalDateTime startDate, LocalDateTime endDate);
    Long countByStartSiteIdAndCreatedDateBetween(Long startSiteId, LocalDateTime startDate, LocalDateTime endDate);
    Long countByEndSiteIdAndHasDeleted(SiteManagerEntity endSiteId, boolean hasDeleted);
    Long countByEndSiteIdAndHasDeletedAndCreatedDateBetween(SiteManagerEntity endSiteId, boolean hasDeleted, LocalDateTime startDate, LocalDateTime endDate);
    Long countByEndSiteIdAndCreatedDateBetween(Long endSiteId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<ContractEntity> findByIdAndHasDeleted(Long id, boolean hasDeleted);

}
