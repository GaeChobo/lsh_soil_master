package kr.movements.smv2.repository;

import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.DriverEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaybillRepositoryCustom {

    TransportUnprocessedDetailDto getTransportUnprocessedDetail(Long waybillId);
    Page<ContractDeleteInfoDto> findWaybillWithContract(Long contractId, String waybillStatus, Pageable pageable);

    Page<WaybillListDto> findAllWaybillList(Long siteManagerId, String siteType, String searchType, String content, String waybillStatus, Pageable pageable);
    Page<WaybillListDto> findAllAdminWaybillList(String searchType, String keyword, String waybillStatus, Pageable pageable);

//    List<DriverWayBillListResponse> findDriverWayBillList(Long driverId, String startSiteName, String endSiteName,
//                                                    String materialTypeCode, String waybillStatusCode,
//                                                          LocalDateTime startDate, LocalDateTime endDate);
    Long findByNotPassCount(Long siteManagerId, String siteType, Long userId);

    Long findByDriverTotalPassCount(Long driverId);

    Page<TransportRealTimeListDto> findByTransportRealTimeList(Long siteManagerId, String siteType, Long userId, String searchType, String keyword, Pageable pageable);
    Page<TransportDailyNotPassListResponse> findByDailyNotPassList(Long siteManagerId, String siteType, Long userId, String searchType, String keyword, Pageable pageable);
    Page<TransportUnprocessedListDto> findAllTransportUnprocessedList(Long siteManagerId, String siteType, String searchType, String keyword, Pageable pageable);

    Integer sumByTosaLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByStoneLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumBySandLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByPebbleLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByCrashedStoneLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByStonePowderLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByMixedAggregateLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByRubbleLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate);
//    Integer sumByRubbleLocalDate(List<ContractEntity> contract, LocalDateTime startDate, LocalDateTime endDate);
    Integer sumByMaterialVolume(Long contractId, String materialTypeCode);
    Long driverDailyUnloadCount(Long userId);
//    Integer countByDriverAndHasDeletedAndWaybillStatusCodeOrWaybillStatusCodeAndArriveTimeBetween(DriverEntity driver, boolean hasDeleted, String waybillStatusCode, @Param("waybillStatusCode") String waybillStatusCode2, LocalDateTime startDate, LocalDateTime endDate);
    Page<TransportNotPassInfoResponse> transportNotPassInfo(Long driverId, Pageable pageable);

    List<DriverWayBillListResponse> findAllDriverWaybillList(Long driverId, String startSiteName, String endSiteName, String materialTypeCode, String waybillStatusCode, LocalDateTime startDate, LocalDateTime endDate);
}