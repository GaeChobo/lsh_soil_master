package kr.movements.smv2.repository;

import kr.movements.smv2.common.util.StringBuilderQuery;
import kr.movements.smv2.dto.AppWaybillList;
import kr.movements.smv2.dto.DriverWayBillListResponse;
import io.swagger.models.auth.In;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.DriverEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.WaybillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.*;

public interface WaybillRepository extends JpaRepository<WaybillEntity, Long>, WaybillRepositoryCustom {

    Optional<WaybillEntity> findByDriverIdAndHasDeleted(Long driverId, boolean hasDeleted);

    List<WaybillEntity> findAllByDriverIdAndHasDeleted(Long driverId, Boolean hasDeleted);

    Optional<WaybillEntity> findByIdAndHasDeleted(Long waybillId, boolean hasDeleted);
    Optional<WaybillEntity> findByIdAndDriverAndHasDeleted(Long waybillId, DriverEntity driver,  boolean hasDeleted);

    boolean existsByContractAndWaybillNum(ContractEntity contract, Integer waybillNum);

    boolean existsByContractAndWaybillNum(ContractEntity contract, String waybillNum);

    @Query(value = "SELECT distinct start_site_name as startSiteName\n" +
            "FROM waybill\n" +
            "WHERE has_deleted = false AND driver_id = :driverId" ,nativeQuery = true)
    List<Map<String, Object>> waybillStartSiteList(@Param("driverId") Long driverId);

    @Query(value = "SELECT distinct end_site_name as endSiteName\n" +
            "FROM waybill\n" +
            "WHERE has_deleted = false AND driver_id = :driverId", nativeQuery = true)
    List<Map<String, Object>> waybillEndSiteList(@Param("driverId") Long driverId);

    @Modifying
    @Query(value = "update waybill \n" +
            "set \n" +
            "waybill_status_code = :waybillStatusCode, \n" +
            "departure_time = :departureTime, \n" +
            "ete = :ete, \n" +
            "modified_date = :modifiedDate, \n" +
            "gps_agree = :gpsAgree \n" +
            "where \n" +
            "id = :waybillId \n" +
            "and has_deleted = false", nativeQuery = true)
    void waybillStatusStart(@Param("waybillStatusCode") String waybillStatusCode ,@Param("departureTime") LocalDateTime departureTime, @Param("ete")Integer ete , @Param("modifiedDate") LocalDateTime modifiedDate,@Param("gpsAgree") Boolean gpsAgree ,@Param("waybillId") Long waybillId);

    @Modifying
    @Query(value = "update waybill set\n" +
            "waybill_status_code = :waybillStatusCode,\n" +
            "wait_time = :waitTime,\n" +
            "modified_date = :modifiedDate \n" +
            "where id = :waybillId and has_deleted = false", nativeQuery = true)
    void waybillStatusWait(@Param("waybillStatusCode") String waybillStatusCode, @Param("waitTime") LocalDateTime waitTime, @Param("modifiedDate") LocalDateTime modifiedDate ,@Param("waybillId") Long waybillId);

    @Modifying
    @Query(value = "update waybill set\n" +
            "\twaybill_status_code = :waybillStatusCode\n" +
            "where id = :waybillId and has_deleted = false", nativeQuery = true)
    void waybillStatusUpdate(@Param("waybillStatusCode") String waybillStatusCode, @Param("waybillId") Long waybillId);

    @Modifying
    @Query(value = "update waybill set\n" +
            "waybill_status_code = :waybillStatusCode,\n" +
            "arrive_time = :arriveTime,\n" +
            "modified_date = :modifiedDate \n" +
            "where id = :waybillId and has_deleted = false", nativeQuery = true)
    void waybillArriveUpdate(@Param("waybillStatusCode") String waybillStatusCode, @Param("arriveTime") LocalDateTime arriveTime, @Param("modifiedDate") LocalDateTime modifiedDate ,@Param("waybillId") Long waybillId);



    @Query(value = "select \n" +
            "\ttransport_volume\n" +
            "from waybill\n" +
            "where material_type_code = :materialTypeCode and contract_id = :contractId and has_deleted = false and " +
            "(waybill_status_code = '3010' or waybill_status_code = '3020' or waybill_status_code = '3030' or waybill_status_code = '3040' or waybill_status_code = '3050')", nativeQuery = true)
    List<Integer> contractTransportVolume(@Param("materialTypeCode") String materialTypeCode, @Param("contractId") Long contractId);

    @Query(value = "select \n" +
            "\tcount(*)\n" +
            "from\n" +
            "\twaybill\n" +
            "where has_deleted = false and contract_id = :contractID", nativeQuery = true)
    int contractUseAt(@Param("contractID") Long contractId);

    @Query(value = "select \n" +
            "\tcontract_id \n" +
            "from waybill w\n" +
            "where id = :waybillId", nativeQuery = true)
    Long waybillFindContractID(@Param("waybillId") Long waybillId);

//    Optional<WaybillEntity> findByContractIdAndHasDeleted(Long contractId, boolean hasDeleted);
    List<WaybillEntity> findByContractAndHasDeleted(ContractEntity contract, boolean hasDeleted);

    Optional<WaybillEntity> findTopByContractOrderByWaybillNumDesc(ContractEntity contract);
    Long countByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Optional<WaybillEntity> findTopByDriverAndHasDeletedOrderByCreatedDateDesc(DriverEntity driver, boolean hasDeleted);
    Integer countByDriver(DriverEntity driver);
    Integer countByContractAndHasDeleted(ContractEntity contract, boolean hasDeleted);
    Integer countByContractAndHasDeletedAndCreatedDateBetween(ContractEntity contract, boolean hasDeleted, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByContractAndMaterialTypeCodeAndHasDeleted(ContractEntity contract, String materialTypeCode, boolean hasDeleted);
    boolean existsByContractAndHasDeleted(ContractEntity contract, boolean hasDeleted);
}
