package kr.movements.smv2.repository;

import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.DriverEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface SiteManagerRepository extends JpaRepository<SiteManagerEntity, Long>, SiteManagerRepositoryCustom{





    Optional<SiteManagerEntity> findByIdAndHasDeleted(Long siteId, Boolean hasDeleted);
/*
    @Modifying
    @Query(value = "update site_manager set\n" +
            "\tfirst_login = true \n" +
            "where id = :siteId and has_deleted = false", nativeQuery = true)
    void siteManagerFirstLogin(@Param("siteId") Long siteId);

 */

    @Query(value = "select \n" +
            "\tid as siteId,\n" +
            "\tlatitude as latitude,\n" +
            "\tlongitude as longitude\n" +
            "from site_manager sm\n" +
            "where id = :siteId and has_deleted = false ", nativeQuery = true)
    List<SiteAreaMapInfo> mySiteLocation(@Param("siteId") Long siteId);

    @Query(value = "SELECT\n" +
            "    user_info.id as userId,\n" +
            "    user_info.user_phone as userPhone,\n" +
            "    user_info.user_name as userName,\n" +
            "    user_info.user_email as userEmail,\n" +
            "    site_manager.id as siteId,\n" +
            "    site_manager.site_type_code as siteTypeCode,\n" +
            "    site_manager.site_name as siteName\n" +
            "FROM soil_master.public.user_info\n" +
            "INNER JOIN site_manager ON site_manager.user_id = user_info.id\n" +
            "WHERE user_info.has_deleted = false \n" +
            "    AND site_manager.has_deleted = false \n" +
            "    AND site_manager.site_name LIKE %:siteName%\n" +
            "    AND site_manager.site_type_code LIKE %:siteTypeCode%\n" +
            "ORDER BY site_manager.created_date DESC" , nativeQuery = true)
    Page<SiteManagerList> searchOfSiteNameManagerList(Pageable pageable, @Param("siteName") String siteName, @Param("siteTypeCode") String siteTypeCode);

    @Query(value = "SELECT\n" +
            "    user_info.id as userId,\n" +
            "    user_info.user_phone as userPhone,\n" +
            "    user_info.user_name as userName,\n" +
            "    user_info.user_email as userEmail,\n" +
            "    site_manager.id as siteId,\n" +
            "    site_manager.site_type_code as siteTypeCode,\n" +
            "    site_manager.site_name as siteName\n" +
            "FROM soil_master.public.user_info\n" +
            "INNER JOIN site_manager ON site_manager.user_id = user_info.id\n" +
            "WHERE user_info.has_deleted = false \n" +
            "    AND site_manager.has_deleted = false \n" +
            "    AND user_info.user_name LIKE %:userName%\n" +
            "    AND site_manager.site_type_code LIKE %:siteTypeCode%\n" +
            "ORDER BY site_manager.created_date DESC" , nativeQuery = true)
    Page<SiteManagerList> searchOfUserNameManagerList(Pageable pageable, @Param("userName") String userName, @Param("siteTypeCode") String siteTypeCode);

    @Query(value = "SELECT\n" +
            "    user_info.id as userId,\n" +
            "    user_info.user_phone as userPhone,\n" +
            "    user_info.user_name as userName,\n" +
            "    user_info.user_email as userEmail,\n" +
            "    site_manager.id as siteId,\n" +
            "    site_manager.site_type_code as siteTypeCode,\n" +
            "    site_manager.site_name as siteName\n" +
            "FROM soil_master.public.user_info\n" +
            "INNER JOIN site_manager ON site_manager.user_id = user_info.id\n" +
            "WHERE user_info.has_deleted = false \n" +
            "    AND site_manager.has_deleted = false \n" +
            "    AND user_info.user_phone LIKE %:userPhone%\n" +
            "    AND site_manager.site_type_code LIKE %:siteTypeCode%\n" +
            "ORDER BY site_manager.created_date DESC", nativeQuery = true)
    Page<SiteManagerList> searchOfUserPhoneManagerList(Pageable pageable, @Param("userPhone") String userPhone, @Param("siteTypeCode") String siteTypeCode);

    @Query(value = "SELECT\n" +
            "    user_info.id AS userId,\n" +
            "    user_info.user_phone AS userPhone,\n" +
            "    user_info.user_name AS userName,\n" +
            "    user_info.user_email AS userEmail,\n" +
            "    site_manager.id AS siteId,\n" +
            "    site_manager.site_type_code AS siteTypeCode,\n" +
            "    site_manager.site_name AS siteName\n" +
            "FROM\n" +
            "    user_info\n" +
            "JOIN\n" +
            "    site_manager ON site_manager.user_id = user_info.id\n" +
            "WHERE\n" +
            "    user_info.has_deleted = false\n" +
            "    AND site_manager.has_deleted = false\n" +
            "    AND site_manager.site_type_code LIKE %:siteTypeCode%\n" +
            "ORDER BY\n" +
            "    site_manager.created_date DESC",
            countQuery = "SELECT COUNT(*) FROM user_info JOIN site_manager ON site_manager.user_id = user_info.id WHERE user_info.has_deleted = false AND site_manager.has_deleted = false",
            nativeQuery = true)
    Page<SiteManagerList> siteManagerList(Pageable pageable, @Param("siteTypeCode") String siteTypeCode);

    @Query(value = "select\n" +
            "\tsite_manager.id as siteId,\n" +
            "\tsite_manager.site_name as siteName,\n" +
            "\tsite_manager.site_type_code as siteTypeCode,\n" +
            "\tsite_manager.address as address,\n" +
            "\tsite_manager.weight_bridge_type as weighBridgeType,\n" +
            "\tuser_info.user_name as userName,\n" +
            "\tuser_info.user_phone as userPhone,\n" +
            "\tuser_info.user_email as userEmail,\n" +
            "\tsite_manager_certification.site_certification_pw as siteCertificationPw\n" +
//            "\tsite_manager.first_login as firstLogin\n" +
            "from site_manager\n" +
            "inner join user_info on site_manager.user_id = user_info.id\n" +
            "inner join site_manager_certification on site_manager.id = site_manager_certification.site_id\n" +
            "where user_info.id = :userId and site_manager.has_deleted = false", nativeQuery = true)
    SiteManagerInfo siteManagerInfo(@Param("userId") Long userId);

    @Modifying
    @Query(value = "update site_manager set\n" +
            "\tweight_bridge_type = :weighbridgeType\n" +
            "\twhere user_id = :userId and has_deleted = false", nativeQuery = true)
    void adminSiteManagerUpdate(@Param("weighbridgeType") boolean weighBridgeType, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update site_manager set\n" +
            "\tweight_bridge_type = :weighbridgeType\n" +
            "\twhere user_id = :userId and has_deleted = false", nativeQuery = true)
    void siteManagerInfoUpdate(@Param("weighbridgeType") boolean weighBridgeType, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update site_manager set\n" +
            "\tactivation = true\n" +
            "where user_id = :userId and has_deleted = false", nativeQuery = true)
    void siteManagerActivationUpdate(@Param("userId") Long userId);

    @Modifying
    @Query(value = "update site_manager set\n" +
            "\thas_deleted = true\n" +
            "where user_id = :userId", nativeQuery = true)
    void siteManagerDelete(@Param("userId") Long userId);

    Optional<SiteManagerEntity> findByUserIdAndHasDeleted(UserInfoEntity userInfoId, Boolean hasDeleted);

    @Query(value = "SELECT\n" +
            "    sm.id AS siteId,\n" +
            "    sm.address AS address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    sm.latitude AS latitude,\n" +
            "    sm.longitude AS longitude,\n" +
            "    CEIL(6371000 * 2 * ASIN(SQRT(\n" +
            "        POWER(SIN((RADIANS(:latitude) - RADIANS(sm.latitude)) / 2), 2) +\n" +
            "        COS(RADIANS(:latitude)) * COS(RADIANS(sm.latitude)) *\n" +
            "        POWER(SIN((RADIANS(:longitude) - RADIANS(sm.longitude)) / 2), 2)\n" +
            "    ))) AS distance\n" +
            "FROM\n" +
            "    site_manager sm\n" +
            "WHERE\n" +
            "    sm.site_type_code = :siteTypeCode\n" +
            "    AND sm.has_deleted = false\n" +
            "    AND (sm.address LIKE '%' || :keyword || '%' OR sm.site_name LIKE '%' || :keyword || '%' OR sm.road_address LIKE '%' || :keyword || '%')\n" +
            "ORDER BY\n" +
            "    distance ASC", nativeQuery = true)
    Page<SiteAreaInfo> siteAddressSearch(@Param("latitude") Double latitude, @Param("longitude") Double longitude ,@Param("siteTypeCode")String siteTypeCode, @Param("keyword") String keyword, Pageable pageable);


    @Query(value = "SELECT \n" +
            "    sm.id AS siteId,\n" +
            "    sm.address AS address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    c.id AS contractId\n" +
            "FROM site_manager sm\n" +
            "INNER JOIN contract c ON c.start_site_id = sm.id OR c.end_site_id = sm.id\n" +
            "WHERE \n" +
            "    sm.site_type_code = :siteTypeCode\n" +
            "    AND sm.has_deleted = false\n" +
            "    AND (sm.address LIKE %:keyword% OR sm.site_name LIKE %:keyword% OR sm.road_address LIKE %:keyword%)\n" +
            "    AND (c.start_site_id = :siteId OR c.end_site_id = :siteId)", nativeQuery = true)
    Page<SiteAreaInfo> waybillAddressSearch(@Param("siteTypeCode")String siteTypeCode, @Param("keyword") String keyword, @Param("siteId") Long siteId,Pageable pageable);


    @Query(value = "SELECT\n" +
            "    c.end_site_id AS siteId,\n" +
            "    sm.address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    sm.latitude,\n" +
            "    sm.longitude\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager sm ON sm.id = c.end_site_id\n" +
            "WHERE\n" +
            "    sm.has_deleted = false\n" +
            "    AND start_site_id = :startSiteId\n" +
            "    AND (\n" +
            "        sm.address LIKE '%' || :keyword || '%'\n" +
            "        OR sm.site_name LIKE '%' || :keyword || '%'\n" +
            "        OR sm.road_address LIKE '%' || :keyword || '%'\n" +
            "    )\n" +
            "ORDER BY\n" +
            "    c.created_date ASC", nativeQuery = true)
    List<SiteAreaMapInfo> waybillEndSiteMapList(@Param("startSiteId") Long siteId, @Param("keyword") String keyword);

    @Query(value = "SELECT\n" +
            "    c.start_site_id AS siteId,\n" +
            "    sm.address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    sm.latitude,\n" +
            "    sm.longitude\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager sm ON sm.id = c.start_site_id\n" +
            "WHERE\n" +
            "    sm.has_deleted = false\n" +
            "    AND end_site_id = :endSiteId\n" +
            "    AND (\n" +
            "        sm.address LIKE '%' || :keyword || '%'\n" +
            "        OR sm.site_name LIKE '%' || :keyword || '%'\n" +
            "        OR sm.road_address LIKE '%' || :keyword || '%'\n" +
            "    )\n" +
            "ORDER BY\n" +
            "    c.created_date ASC", nativeQuery = true)
    List<SiteAreaMapInfo> waybillStartSiteMapList(@Param("endSiteId") Long siteId, @Param("keyword") String keyword);

    @Query(value = "SELECT\n" +
            "    c.end_site_id AS siteId,\n" +
            "    sm.address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    CEIL(6371000 * 2 * ASIN(SQRT(\n" +
            "        POWER(SIN((RADIANS(:latitude) - RADIANS(sm.latitude)) / 2), 2) +\n" +
            "        COS(RADIANS(:latitude)) * COS(RADIANS(sm.latitude)) *\n" +
            "        POWER(SIN((RADIANS(:longitude) - RADIANS(sm.longitude)) / 2), 2)\n" +
            "    ))) AS distance\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager sm ON sm.id = c.end_site_id\n" +
            "WHERE\n" +
            "    sm.has_deleted = false\n" +
            "    AND c.start_site_id = :startSiteId\n" +
            "    AND (\n" +
            "        sm.address LIKE '%' || :keyword || '%'\n" +
            "        OR sm.site_name LIKE '%' || :keyword || '%'\n" +
            "        OR sm.road_address LIKE '%' || :keyword || '%'\n" +
            "    )\n" +
            "ORDER BY\n" +
            "    distance ASC", nativeQuery = true)
    Page<SiteAreaAddressInfo> waybillEndSiteAddressList(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("startSiteId") Long startSiteId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT\n" +
            "    c.start_site_id AS siteId,\n" +
            "    sm.address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    CEIL(6371000 * 2 * ASIN(SQRT(\n" +
            "        POWER(SIN((RADIANS(:latitude) - RADIANS(sm.latitude)) / 2), 2) +\n" +
            "        COS(RADIANS(:latitude)) * COS(RADIANS(sm.latitude)) *\n" +
            "        POWER(SIN((RADIANS(:longitude) - RADIANS(sm.longitude)) / 2), 2)\n" +
            "    ))) AS distance\n" +
            "FROM\n" +
            "    contract c\n" +
            "INNER JOIN\n" +
            "    site_manager sm ON sm.id = c.start_site_id\n" +
            "WHERE\n" +
            "    sm.has_deleted = false\n" +
            "    AND c.end_site_id = :endSiteId\n" +
            "    AND (\n" +
            "        sm.address LIKE '%' || :keyword || '%'\n" +
            "        OR sm.site_name LIKE '%' || :keyword || '%'\n" +
            "        OR sm.road_address LIKE '%' || :keyword || '%'\n" +
            "    )\n" +
            "ORDER BY\n" +
            "    distance ASC", nativeQuery = true)
    Page<SiteAreaAddressInfo> waybillStartSiteAddressList(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("endSiteId") Long endSiteId, @Param("keyword") String keyword, Pageable pageable);


    /*
    @Query(value = "SELECT\n" +
            "    sm.id AS siteId,\n" +
            "    sm.address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    sm.latitude,\n" +
            "    sm.longitude\n" +
            "FROM site_manager sm\n" +
            "INNER JOIN contract c\n" +
            "    ON sm.id IN (c.start_site_id, c.end_site_id)\n" +
            "WHERE ST_Distance(\n" +
            "        CAST(ST_MakePoint(sm.longitude, sm.latitude) AS geography),\n" +
            "        CAST(ST_MakePoint(:longitude, :latitude) AS geography)\n" +
            "    ) <= 3000 -- 3km\n" +
            "    AND sm.has_deleted = false\n" +
            "    AND sm.site_type_code = :siteTypeCode\n" +
            "    AND :siteId IN (c.start_site_id, c.end_site_id)", nativeQuery = true)
    List<SiteAreaMapInfo> waybillMapList(@Param("longitude")double longitude, @Param("latitude") double latitude, @Param("siteTypeCode") String siteTypeCode, @Param("siteId") Long siteId);
    */

    @Query(value = "SELECT \n" +
            "    sm.id AS siteId,\n" +
            "    sm.address AS address,\n" +
            "    sm.site_name AS siteName,\n" +
            "    sm.road_address AS roadAddress,\n" +
            "    sm.zip_code AS zipCode,\n" +
            "    sm.address_detail AS addressDetail,\n" +
            "    sm.latitude as latitude,\n" +
            "    sm.longitude as longitude\n" +
            "FROM \n" +
            "    site_manager sm\n" +
            "    INNER JOIN contract c ON c.start_site_id = sm.id OR c.end_site_id = sm.id\n" +
            "WHERE \n" +
            "    sm.site_type_code = :siteTypeCode\n" +
            "    AND sm.has_deleted = false\n" +
            "    AND (\n" +
            "        sm.address LIKE %:keyword% \n" +
            "        OR sm.site_name LIKE %:keyword% \n" +
            "        OR sm.road_address LIKE %:keyword%\n" +
            "    )\n" +
            "    AND (c.start_site_id = :siteId OR c.end_site_id = :siteId)" , nativeQuery = true)
    List<SiteAreaMapInfo> waybillSiteMapSearch(@Param("siteTypeCode")String siteTypeCode, @Param("keyword") String keyword, @Param("siteId") Long siteId);

    //이것도 문제없음
    @Query(value = "select\n" +
            "\tsm.id as siteId,\n" +
            "\tsm.address as address,\n" +
            "\tsm.site_name as siteName,\n" +
            "\tsm.road_address as roadAddress,\n" +
            "\tsm.zip_code as zipCode,\n" +
            "\tsm.address_detail as addressDetail,\n" +
            "\tsm.latitude as latitude,\n" +
            "\tsm.longitude as longitude\n" +
            "from site_manager sm\n" +
            "where site_type_code = :siteTypeCode and has_deleted = false and address like %:address% or road_address like %:roadAddress% or site_name like %:siteName%"
            , nativeQuery = true)
    List<SiteAreaMapInfo> siteMapSearch(@Param("siteTypeCode")String siteTypeCode, @Param("address") String address, @Param("roadAddress") String roadAddress, @Param("siteName") String siteName);

    /*
    @Query(value = "SELECT\n" +
            "sm.id AS siteId,\n" +
            "sm.address,\n" +
            "sm.site_name AS siteName,\n" +
            "sm.road_address AS roadAddress,\n" +
            "sm.zip_code AS zipCode,\n" +
            "sm.address_detail AS addressDetail,\n" +
            "sm.latitude,\n" +
            "sm.longitude\n" +
            "FROM\n" +
            "site_manager sm\n" +
            "WHERE\n" +
            "ST_Distance(\n" +
            "CAST(ST_MakePoint(sm.longitude, sm.latitude) AS geography),\n" +
            "CAST(ST_MakePoint(:longitude, :latitude) AS geography)\n" +
            ") <= 3000 -- 3km\n" +
            "AND has_deleted = false\n" +
            "AND site_type_code = :siteTypeCode", nativeQuery = true)
    List<SiteAreaMapInfo> contractMapList(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("siteTypeCode") String siteTypeCode);
    */

    @Query(value = "select\n" +
            "sm.id as siteId,\n" +
            "sm.address as address,\n" +
            "sm.site_name as siteName,\n" +
            "sm.road_address as roadAddress,\n" +
            "sm.zip_code as zipCode,\n" +
            "sm.latitude,\n" +
            "sm.longitude\n" +
            "from site_manager sm\n" +
            "where has_deleted = false and site_type_code = :siteTypeCode", nativeQuery = true)
    List<SiteAreaMapInfo> contractMapList(@Param("siteTypeCode") String siteTypeCode);

    /*
    @Query(value = "SELECT\n" +
            "sm.id AS siteId,\n" +
            "sm.address AS address,\n" +
            "sm.site_name AS siteName,\n" +
            "sm.road_address AS roadAddress,\n" +
            "sm.zip_code AS zipCode,\n" +
            "sm.address_detail AS addressDetail,\n" +
            "sm.latitude AS latitude,\n" +
            "sm.longitude AS longitude\n" +
            "FROM site_manager sm\n" +
            "INNER JOIN contract c ON (sm.id = c.start_site_id OR sm.id = c.end_site_id)\n" +
            "WHERE\n" +
            "ST_Distance(\n" +
            "CAST(ST_MakePoint(sm.longitude, sm.latitude) AS geography),\n" +
            "CAST(ST_MakePoint(:longitude, :latitude) AS geography)\n" +
            ") <= 3000 -- 3km\n" +
            "AND sm.has_deleted = false\n" +
            "AND sm.site_type_code = :siteTypeCode\n" +
            "AND (c.start_site_id = :siteId OR c.end_site_id = :siteId)")
    List<SiteAreaMapInfo> waybillMapList(@Param("longitude")double longitude, @Param("latitude") double latitude, @Param("siteTypeCode") String siteTypeCode, @Param("siteId") Long siteId);
    */

    @Query(value = "select \n" +
            "\tsm.id as siteId,\n" +
            "\tsm.address as address,\n" +
            "\tsm.site_name as siteName,\n" +
            "\tsm.road_address as roadAddress,\n" +
            "\tsm.zip_code as zipCode,\n" +
            "\tsm.address_detail as addressDetail,\n" +
            "\tsm.latitude as latitude,\n" +
            "\tsm.longitude as longitude\n" +
            "from site_manager sm\n" +
            "WHERE ST_Contains(ST_MakeEnvelope(:longitude1, :latitude2, :longitude2, :latitude1, 4326), geom)\n" +
            "and has_deleted = false\n" +
            "and site_type_code :siteTypeCode\n" +
            "AND latitude IS NOT NULL\n" +
            "AND longitude IS NOT null", nativeQuery = true)
    List<SiteAreaMapInfo> defaultSiteMapSearch(@Param("longitude1") double longitude1, @Param("latitude2") double latitude2,
                                               @Param("latitude2") double longitude2, @Param("latitude1") double latitude1,
                                               @Param("siteTypeCode") String siteTypeCode);

//    List<SiteManagerEntity> findAllBySiteTypeCodeAndHasDeleted(String siteTypeCode, boolean hasDeleted);

    Long countBySiteTypeCodeAndCreatedDateBetween(String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Long countBySiteTypeCode(String siteTypeCode);
//    Page<SearchAddressResponse> findAllSearchAddress(Long siteId, String searchType, String keyword, Pageable pageable);
}
