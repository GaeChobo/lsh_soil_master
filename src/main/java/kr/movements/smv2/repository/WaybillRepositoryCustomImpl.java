package kr.movements.smv2.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.*;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.movements.smv2.entity.QContractEntity.contractEntity;
import static kr.movements.smv2.entity.QDriverEntity.driverEntity;
import static kr.movements.smv2.entity.QFileEntity.fileEntity;
import static kr.movements.smv2.entity.QReasonEntity.reasonEntity;
import static kr.movements.smv2.entity.QSiteManagerEntity.siteManagerEntity;
import static kr.movements.smv2.entity.QUserInfoEntity.userInfoEntity;
import static kr.movements.smv2.entity.QWaybillEntity.waybillEntity;

@RequiredArgsConstructor
public class WaybillRepositoryCustomImpl implements WaybillRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager entityManager;
/*
    @Override
    public List<DriverWayBillListResponse> findDriverWayBillList(Long driverId, String startSiteName, String endSiteName,
                                                                 String materialTypeCode, String waybillStatusCode,
                                                                 LocalDateTime startDate, LocalDateTime endDate) {

        String jpql = "SELECT new kr.movements.smv2.dto.DriverWayBillListResponse( " +
                "to_char(w.modifiedDate, 'YYYY-MM-DD'), " +
                "w.id, " +
                "w.startSiteName, " +
                "w.endSiteName, " +
                "w.waybillNum, " +
                "d.gpsAgree, " +
                "w.waybillStatusCode, " +
                "w.materialTypeCode) " +
                "FROM kr.movements.smv2.entity.WaybillEntity w " +
                "JOIN w.driver d " +
                "JOIN w.contract c " +
                "WHERE w.hasDeleted = false " +
                "AND w.driver.id = :driverId " +
                "AND w.modifiedDate BETWEEN :startDate AND :endDate ";

        if (startSiteName != null && !startSiteName.isEmpty()) {
            jpql += "AND w.startSiteName = :startSiteName ";
        }
        if (endSiteName != null && !endSiteName.isEmpty()) {
            jpql += "AND w.endSiteName = :endSiteName ";
        }
        if (materialTypeCode != null && !materialTypeCode.isEmpty()) {
            jpql += "AND w.materialTypeCode = :materialTypeCode ";
        }
        if (waybillStatusCode != null && !waybillStatusCode.isEmpty()) {
            jpql += "AND w.waybillStatusCode = :waybillStatusCode ";
        }

        jpql += " ORDER BY w.modifiedDate DESC";

        TypedQuery<DriverWayBillListResponse> query = entityManager.createQuery(jpql, DriverWayBillListResponse.class);
        query.setParameter("driverId", driverId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        if (startSiteName != null && !startSiteName.isEmpty()) {
            query.setParameter("startSiteName", startSiteName);
        }
        if (endSiteName != null && !endSiteName.isEmpty()) {
            query.setParameter("endSiteName", endSiteName);
        }
        if (materialTypeCode != null && !materialTypeCode.isEmpty()) {
            query.setParameter("materialTypeCode", materialTypeCode);
        }
        if (waybillStatusCode != null && !waybillStatusCode.isEmpty()) {
            query.setParameter("waybillStatusCode", waybillStatusCode);
        }

        List<DriverWayBillListResponse> result = query.getResultList();

        return result;
    }

 */
    @Override
    public TransportUnprocessedDetailDto getTransportUnprocessedDetail(Long waybillId) {
        QWaybillEntity waybill = QWaybillEntity.waybillEntity;
        QDriverEntity driver = QDriverEntity.driverEntity;
        QUserInfoEntity userInfo = QUserInfoEntity.userInfoEntity;
        QContractEntity contract = QContractEntity.contractEntity;
        QSiteManagerEntity startSite = new QSiteManagerEntity("sm1");
        QSiteManagerEntity endSite = new QSiteManagerEntity("sm2");
        QReasonEntity reason = QReasonEntity.reasonEntity;

        TransportUnprocessedDetailDto result = jpaQueryFactory
                .select(new QTransportUnprocessedDetailDto(
                        userInfo.userName,
                        userInfo.userPhone,
                        driver.carNumber,
                        driver.driverCompany,
                        waybill.waybillNum,
                        startSite.siteName,
                        endSite.siteName,
                        waybill.materialTypeCode,
                        waybill.transportVolume,
                        waybill.departureTime,
                        waybill.arriveTime,
                        reason.reason
                ))
                .from(waybill)
                .join(driver).on(waybill.driver.eq(driver))
                .join(userInfo).on(driver.userInfoId.eq(userInfo))
                .join(contract).on(waybill.contract.eq(contract))
                .join(startSite).on(contract.startSiteId.eq(startSite))
                .join(endSite).on(contract.endSiteId.eq(endSite))
                .leftJoin(reason).on(waybill.id.eq(reason.waybill.id))
                .where(
                        waybill.id.eq(waybillId),
                        waybill.hasDeleted.eq(false),
                        waybill.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_QUIT.getCode())
                )
                .fetchOne();

        if (result != null) {
            String materialTypeCode = result.getMaterialTypeCode();
            if (materialTypeCode != null) {
                String materialValue = getMaterialValue(materialTypeCode);
                result.setMaterialValue(materialValue);
            }
        }

        return result;
    }

    private String getMaterialValue(String materialTypeCode) {
        Map<String, String> materialMapping = getMaterialMapping();
        return materialMapping.getOrDefault(materialTypeCode, "");
    }

    @Override
    public Page<ContractDeleteInfoDto> findWaybillWithContract(Long contractId, String waybillStatus, Pageable pageable) {
        QWaybillEntity waybill = QWaybillEntity.waybillEntity;

        Map<String, String> statusMapping = getStatusMapping(); // 상태 코드와 값 매핑

        List<Tuple> results = jpaQueryFactory
                .select(waybill.id, waybill.waybillNum, waybill.waybillStatusCode, waybill.carNumber, waybill.materialTypeCode, waybill.transportVolume)
                .from(waybill)
                .where(waybill.hasDeleted.eq(false)
                        .and(waybill.contract.id.eq(contractId))
                        .and(searchWaybillStatus(waybillStatus)))
                .orderBy(waybill.modifiedBy.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(waybill.count())
                .from(waybill)
                .where(waybill.hasDeleted.eq(false)
                        .and(waybill.contract.id.eq(contractId))
                        .and(searchWaybillStatus(waybillStatus)))
                .fetchCount();

        List<ContractDeleteInfoDto> mappedResults = results.stream()
                .map(tuple -> {
                    Long waybillId = tuple.get(waybill.id);
                    String waybillNum = tuple.get(waybill.waybillNum);
                    String waybillStatusCode = tuple.get(waybill.waybillStatusCode);
                    String waybillStatusValue = statusMapping.get(waybillStatusCode);
                    String carNumber = tuple.get(waybill.carNumber);
                    String materialCode = tuple.get(waybill.materialTypeCode);
                    String materialValue = getMaterialMapping().get(tuple.get(waybill.materialTypeCode));
                    Integer transportVolume = tuple.get(waybill.transportVolume);
                    return new ContractDeleteInfoDto(waybillStatusCode, waybillStatusValue, waybillNum, carNumber, materialCode, materialValue, transportVolume, waybillId);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(mappedResults, pageable, total);
    }

    private Map<String, String> getMaterialMapping() {
        Map<String, String> materialMapping = new HashMap<>();
        materialMapping.put("5010", "토사");
        materialMapping.put("5020", "암");
        materialMapping.put("5030", "모래");
        materialMapping.put("5040", "자갈");
        materialMapping.put("5050", "파쇄석");
        materialMapping.put("5060", "석분");
        materialMapping.put("5070", "혼합골재");
        materialMapping.put("5080", "잡석");
        return materialMapping;
    }

    private Map<String, String> getStatusMapping() {
        Map<String, String> statusMapping = new HashMap<>();
        statusMapping.put("3010", "송장등록");
        statusMapping.put("3020", "이동중");
        statusMapping.put("3030", "검수대기");
        statusMapping.put("3040", "완료");
        statusMapping.put("3050", "지연완료");
        statusMapping.put("3060", "불통과");
        statusMapping.put("3070", "강제종료");
        return statusMapping;
    }

    @Override
    public Page<WaybillListDto> findAllWaybillList(Long siteManagerId, String siteType, String searchType, String keyword, String waybillStatus, Pageable pageable) {
        List<WaybillListDto> waybillListDtos = getWaybillList(siteManagerId, siteType, searchType, keyword, waybillStatus, pageable);
        Long getCount = getWaybillCount(siteManagerId, siteType, searchType, keyword, waybillStatus);

        return new PageImpl<>(waybillListDtos, pageable, getCount);
    }

    @Override
    public Page<WaybillListDto> findAllAdminWaybillList(String searchType, String keyword, String waybillStatus, Pageable pageable) {
        List<WaybillListDto> waybillListDtos = getAdminWaybillList(searchType, keyword, waybillStatus, pageable);
        Long getCount = getAdminWaybillCount(searchType, keyword, waybillStatus);

        return new PageImpl<>(waybillListDtos, pageable, getCount);
    }

    @Override
    public Long findByDriverTotalPassCount(Long driverId) {
        QWaybillEntity waybill = QWaybillEntity.waybillEntity;

        BooleanExpression condition = waybill.driver.id.eq(driverId)
                .and(waybill.hasDeleted.eq(false))
                .and(waybill.waybillStatusCode.in(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), CommonCode.WAYBILL_STATUS_DELAY.getCode())); // waybill_status_code가 '3040' 또는 '3050'인 경우

        return jpaQueryFactory
                .select(waybill.count())
                .from(waybill)
                .where(condition)
                .fetchOne();
    }

    @Override
    public Long findByNotPassCount(Long siteManagerId, String siteType, Long userId) {
        if(siteType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
            return jpaQueryFactory
                    .select(waybillEntity.count())
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchTodayNotPass(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)
                    ).fetchOne();
        }else {
            return jpaQueryFactory
                    .select(waybillEntity.count())
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.endSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchTodayNotPass(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)
                    ).fetchOne();
        }
    }

    @Override
    public Page<TransportRealTimeListDto> findByTransportRealTimeList(Long siteManagerId, String siteType, Long userId, String searchType, String keyword, Pageable pageable) {
        List<TransportRealTimeListDto> transportRealTimeListDtos = getTransportRealTimeList(siteManagerId, siteType, searchType, keyword, pageable);
        Long getCount = getTransportRealTimeCount(siteManagerId, siteType, searchType, keyword);

        return new PageImpl<>(transportRealTimeListDtos, pageable, getCount);
    }

    @Override
    public Page<TransportDailyNotPassListResponse> findByDailyNotPassList(Long siteManagerId, String siteType, Long userId, String searchType, String keyword, Pageable pageable) {
        List<TransportDailyNotPassListResponse> transportDailyNotPassList = getTransportDailyNotPassList(siteManagerId, siteType, searchType, keyword,pageable);
        Long getCount = getDailyNotPassCount(siteManagerId, siteType, searchType, keyword);

        return new PageImpl<>(transportDailyNotPassList, pageable, getCount);
    }

    @Override
    public Page<TransportUnprocessedListDto> findAllTransportUnprocessedList(Long siteManagerId, String siteType, String searchType, String keyword, Pageable pageable) {
        List<TransportUnprocessedListDto> transportUnprocessedList = getTransportUnprocessedList(siteManagerId, siteType, searchType, keyword,pageable);
        Long getCount = getTransportUnprocessedCount(siteManagerId, siteType, searchType, keyword);

        return new PageImpl<>(transportUnprocessedList, pageable, getCount);
    }

    @Override
    public Page<TransportNotPassInfoResponse> transportNotPassInfo(Long driverId, Pageable pageable) {
        List<TransportNotPassInfoResponse> transportNotPassInfo = getTransportNotPassInfo(driverId, pageable);

        Long getCount = getTransportNotPassInfoCount(driverId);

        return new PageImpl<>(transportNotPassInfo, pageable, getCount);
    }

    @Override
    public List<DriverWayBillListResponse> findAllDriverWaybillList(Long driverId, String startSiteName, String endSiteName, String materialTypeCode, String waybillStatusCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(new QDriverWayBillListResponse(
                        waybillEntity.modifiedDate,
                        waybillEntity.id,
                        waybillEntity.startSiteName,
                        waybillEntity.endSiteName,
                        waybillEntity.waybillNum,
                        waybillEntity.gpsAgree,
                        waybillEntity.waybillStatusCode,
                        waybillEntity.materialTypeCode
                        ))
                .from(waybillEntity)
                .where(
                        waybillEntity.driver.id.eq(driverId),
                        searchStartSite(startSiteName),
                        searchEndSite(endSiteName),
                        searchMaterialType(materialTypeCode),
                        searchWaybillStatus(waybillStatusCode),
                        waybillEntity.modifiedDate.between(startDate, endDate),
                        waybillEntity.hasDeleted.eq(false))
                .orderBy(waybillEntity.modifiedDate.desc())
                .fetch();
    }

    @Override
    public Integer sumByTosaLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_TOSA.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByStoneLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_STONE.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumBySandLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_SAND.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByPebbleLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_PEBBLE.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByCrashedStoneLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_CRASHED_STONE.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByStonePowderLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_STONE_POWDER.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByMixedAggregateLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_MIXED_AGGREGATE.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByRubbleLocalDate(Long siteId, String siteTypeCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(waybillEntity.materialTypeCode.eq(CommonCode.MATERIAL_RUBBLE.getCode()),
                        searchContract(siteId, siteTypeCode),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()))
                                .or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        waybillEntity.createdDate.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Integer sumByMaterialVolume(Long contractId, String materialTypeCode) {
        return jpaQueryFactory
                .select(waybillEntity.transportVolume.sum())
                .from(waybillEntity)
                .where(
                        waybillEntity.materialTypeCode.eq(materialTypeCode),
                        waybillEntity.contract.id.eq(contractId)
                )
                .fetchOne();
    }

    @Override
    public Long driverDailyUnloadCount(Long userId) {
        return jpaQueryFactory
                .select(waybillEntity.count())
                .from(waybillEntity)
                .join(driverEntity).on(waybillEntity.driver.eq(driverEntity))
                .join(userInfoEntity).on(driverEntity.userInfoId.eq(userInfoEntity))
                .where(
                        waybillEntity.driver.userInfoId.id.eq(userId),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_COMPLETE.getCode()).or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_DELAY.getCode())),
                        searchArriveToday(LocalDate.now())
                )
                .fetchOne();
    }



    private List<WaybillListDto> getWaybillList(Long siteManagerId, String siteType, String searchType, String content, String waybillStatus, Pageable pageable) {
        return jpaQueryFactory
                .select(new QWaybillListDto(
                        waybillEntity.id,
                        waybillEntity.waybillNum,
                        waybillEntity.driver.userInfoId.userName,
                        waybillEntity.driver.userInfoId.userPhone,
                        waybillEntity.departureTime,
                        waybillEntity.arriveTime,
                        waybillEntity.waybillStatusCode
                ))
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, content),
                        searchWaybillStatus(waybillStatus),
                        waybillEntity.hasDeleted.eq(false)

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(waybillEntity.createdDate.desc().nullsLast())
                .fetch();
    }

    private List<WaybillListDto> getAdminWaybillList(String searchType, String content, String waybillStatus, Pageable pageable) {
        return jpaQueryFactory
                .select(new QWaybillListDto(
                        waybillEntity.id,
                        waybillEntity.waybillNum,
                        waybillEntity.driver.userInfoId.userName,
                        waybillEntity.driver.userInfoId.userPhone,
                        waybillEntity.departureTime,
                        waybillEntity.arriveTime,
                        waybillEntity.waybillStatusCode
                ))
                .from(waybillEntity)
                .join(driverEntity).on(waybillEntity.driver.eq(driverEntity))
                .join(userInfoEntity).on(driverEntity.userInfoId.eq(userInfoEntity))
                .where(
                        searchTypeContent(searchType, content),
                        searchWaybillStatus(waybillStatus),
                        waybillEntity.hasDeleted.eq(false)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(waybillEntity.departureTime.desc().nullsLast())
                .fetch();
    }
    private List<TransportRealTimeListDto> getTransportRealTimeList(Long siteManagerId, String siteType, String searchType, String keyword, Pageable pageable) {
        return jpaQueryFactory
                .select(new QTransportRealTimeListDto(
                        waybillEntity.id,
                        waybillEntity.waybillNum,
                        waybillEntity.carNumber,
                        waybillEntity.driver.userInfoId.id,
                        waybillEntity.driver.userInfoId.userName,
                        waybillEntity.departureTime
                ))
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, keyword),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()).or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode())),
                        searchToday(LocalDate.now()),
                        waybillEntity.hasDeleted.eq(false)

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(waybillEntity.createdDate.asc().nullsLast())
                .fetch();
    }
    private List<TransportDailyNotPassListResponse> getTransportDailyNotPassList(Long siteManagerId, String siteType, String searchType, String keyword, Pageable pageable) {
        if (siteType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
            return jpaQueryFactory
                    .select(new QTransportDailyNotPassListResponse(
                            waybillEntity.id,
                            waybillEntity.waybillNum,
                            waybillEntity.startSiteName,
                            waybillEntity.endSiteName,
                            waybillEntity.driver.userInfoId.userName,
                            waybillEntity.carNumber
                    ))
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            searchTypeContent(searchType, keyword),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchToday(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)

                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(waybillEntity.createdDate.desc().nullsLast())
                    .fetch();
        }else {
            return jpaQueryFactory
                    .select(new QTransportDailyNotPassListResponse(
                            waybillEntity.id,
                            waybillEntity.waybillNum,
                            waybillEntity.startSiteName,
                            waybillEntity.endSiteName,
                            waybillEntity.driver.userInfoId.userName,
                            waybillEntity.carNumber
                    ))
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.endSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            searchTypeContent(searchType, keyword),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchToday(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)

                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(waybillEntity.createdDate.desc().nullsLast())
                    .fetch();
        }
    }
    private List<TransportUnprocessedListDto> getTransportUnprocessedList(Long siteManagerId, String siteType, String searchType, String keyword, Pageable pageable) {
        return jpaQueryFactory
                .select(new QTransportUnprocessedListDto(
                        waybillEntity.id,
                        waybillEntity.waybillNum,
                        waybillEntity.carNumber,
                        waybillEntity.driver.userInfoId.userPhone,
                        waybillEntity.driver.userInfoId.id,
                        waybillEntity.driver.userInfoId.userName,
                        waybillEntity.departureTime,
                        waybillEntity.modifiedDate
                ))
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, keyword),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_QUIT.getCode()),
                        waybillEntity.hasDeleted.eq(false)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(waybillEntity.createdDate.desc().nullsLast())
                .fetch();
    }
    private List<TransportNotPassInfoResponse> getTransportNotPassInfo(Long driverId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QTransportNotPassInfoResponse(
                        waybillEntity.id,
                        waybillEntity.waybillNum,
                        waybillEntity.driver.userInfoId.id,
                        waybillEntity.driver.id,
                        waybillEntity.driver.userInfoId.userName,
                        waybillEntity.driver.userInfoId.userPhone,
                        waybillEntity.startSiteName,
                        waybillEntity.endSiteName,
                        waybillEntity.materialTypeCode,
                        waybillEntity.transportVolume,
                        waybillEntity.departureTime,
                        waybillEntity.arriveTime,
                        reasonEntity.reason,
                        reasonEntity.file.id
                ))
                .from(waybillEntity)
                .join(driverEntity).on(waybillEntity.driver.eq(driverEntity))
                .join(userInfoEntity).on(driverEntity.userInfoId.eq(userInfoEntity))
                .leftJoin(reasonEntity).on(reasonEntity.waybill.eq(waybillEntity))
                .leftJoin(fileEntity).on(reasonEntity.file.eq(fileEntity))
                .where(
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                        waybillEntity.driver.id.eq(driverId),
                        waybillEntity.hasDeleted.eq(false)

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(waybillEntity.createdDate.desc().nullsLast())
                .fetch();
    }

    private Long getWaybillCount(Long siteManagerId, String siteType, String searchType, String content, String waybillStatus) {

        return jpaQueryFactory
                .select(waybillEntity.count())
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, content),
                        searchWaybillStatus(waybillStatus),
                        waybillEntity.hasDeleted.eq(false)
                ).fetchOne();
    }
    private Long getAdminWaybillCount(String searchType, String content, String waybillStatus) {

        return jpaQueryFactory
                .select(waybillEntity.count())
                .from(waybillEntity)
                .join(driverEntity).on(waybillEntity.driver.eq(driverEntity))
                .join(userInfoEntity).on(driverEntity.userInfoId.eq(userInfoEntity))
                .where(
                        searchTypeContent(searchType, content),
                        searchWaybillStatus(waybillStatus),
                        waybillEntity.hasDeleted.eq(false)
                ).fetchOne();
    }

    private Long getTransportRealTimeCount(Long siteManagerId, String siteType, String searchType, String keyword) {

        return jpaQueryFactory
                .select(waybillEntity.count())
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, keyword),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_MOVE.getCode()).or(waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_WAIT.getCode())),
                        searchToday(LocalDate.now()),
                        waybillEntity.hasDeleted.eq(false)
                ).fetchOne();
    }

    private Long getDailyNotPassCount(Long siteManagerId, String siteType, String searchType, String keyword) {
        //FIXME : 상 하차지 if문 제거할 수 있도록 고도화 필요
        if(siteType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
            return jpaQueryFactory
                    .select(waybillEntity.count())
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            searchTypeContent(searchType, keyword),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchTodayNotPass(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)
                    ).fetchOne();
        }else {
            return jpaQueryFactory
                    .select(waybillEntity.count())
                    .from(waybillEntity)
                    .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                    .join(siteManagerEntity).on(contractEntity.endSiteId.eq(siteManagerEntity))
                    .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                    .where(
                            contractSiteType(siteManagerId, siteType),
                            searchTypeContent(searchType, keyword),
                            waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                            searchTodayNotPass(LocalDate.now()),
                            waybillEntity.hasDeleted.eq(false)
                    ).fetchOne();
        }
    }
    private Long getTransportUnprocessedCount(Long siteManagerId, String siteType, String searchType, String keyword) {
        return jpaQueryFactory
                .select(waybillEntity.id.count()
                )
                .from(waybillEntity)
                .join(contractEntity).on(contractEntity.eq(waybillEntity.contract))
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .join(userInfoEntity).on(siteManagerEntity.userId.eq(userInfoEntity))
                .where(
                        contractSiteType(siteManagerId, siteType),
                        searchTypeContent(searchType, keyword),
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_QUIT.getCode()),
                        waybillEntity.hasDeleted.eq(false)
                )
                .fetchOne();
    }

    private Long getTransportNotPassInfoCount(Long driverId) {
        return jpaQueryFactory
                .select(waybillEntity.id.count())
                .from(waybillEntity)
                .join(driverEntity).on(waybillEntity.driver.eq(driverEntity))
                .join(userInfoEntity).on(driverEntity.userInfoId.eq(userInfoEntity))
                .where(
                        waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()),
                        waybillEntity.driver.id.eq(driverId),
                        waybillEntity.hasDeleted.eq(false)
                )
                .fetchOne();
    }

    /**
     * 송장의 FK인 계약서 ID 전체를 현재 로그인 한 현장관리자 조건(siteID, siteType)으로 조회
     * @param siteManagerId
     * @param siteType
     * @return
     */
    private BooleanExpression contractSiteType(Long siteManagerId, String siteType) {
        if (siteType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) { //상차지
            return waybillEntity.contract.id.in(jpaQueryFactory.select(contractEntity.id).from(contractEntity).where(contractEntity.startSiteId.id.eq(siteManagerId)));
        }else { //TODO : else if로 변경 필요
            return waybillEntity.contract.id.in(jpaQueryFactory.select(contractEntity.id).from(contractEntity).where(contractEntity.endSiteId.id.eq(siteManagerId)));
        }
    }

    /**
     * 송장상태 조건
     * @param waybillStatus
     * @return
     */
    private BooleanExpression searchWaybillStatus(String waybillStatus) {
        if(waybillStatus == null || waybillStatus.isEmpty()){
            return null;
        }else {
            return waybillEntity.waybillStatusCode.eq(waybillStatus);
        }
    }

    /**
     * 검색조건
     * @param searchType
     * @param keyword
     * @return
     */
    private BooleanExpression searchTypeContent(String searchType, String keyword) {
        BooleanExpression result = null;
        if(StringUtils.hasText(keyword)) {
            if (keyword == null) {
                return null;
            } else if (searchType.equals("송장번호")) {
                result = waybillEntity.waybillNum.contains(keyword);
            } else if (searchType.equals("회원명")) {
                result = waybillEntity.driver.userInfoId.userName.contains(keyword);
            } else if (searchType.equals("연락처")) {
                result = waybillEntity.driver.userInfoId.userPhone.contains(keyword);
            } else if (searchType.equals("차량번호")) {//TODO : 팝업에 목록 뿌릴땐 driver 테이블의 차량번호인지?? waybill의 차량번호인지 확인필요.
//                result = waybillEntity.driver.carNumber.contains(keyword);//TODO : 일일 불통과 팝업, 송장의 차량번호로 조회하는 쿼리를 따로 빼야할듯.
                result = waybillEntity.carNumber.contains(keyword);
            } else if (searchType.equals("상차지명")) {
                result = waybillEntity.startSiteName.contains(keyword);
            } else if (searchType.equals("하차지명")) {
                result = waybillEntity.endSiteName.contains(keyword);
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }


    /**
     * 날짜 조건
     * @param date
     * @return
     */
    private BooleanExpression searchToday(LocalDate date){
        return waybillEntity.departureTime.between(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.MAX));
    }

    private BooleanExpression searchArriveToday(LocalDate date){
        return waybillEntity.arriveTime.between(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.MAX));
    }

    private BooleanExpression searchTodayNotPass(LocalDate date){
        return waybillEntity.modifiedDate.between(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.MAX));
    }
//    private BooleanExpression searchContract(List<ContractEntity> contract){
//        return contract == null ? null : waybillEntity.contract.in(jpaQueryFactory.select(contractEntity.id).from(contractEntity).where(contractEntity.startSiteId.eq(siteId)));
//    }
    private BooleanExpression searchContract(Long siteId, String siteTypeCode){
        if(StringUtils.hasText(siteTypeCode)) {
            if(siteTypeCode.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
                return waybillEntity.contract.id.in(jpaQueryFactory.select(contractEntity.id).from(contractEntity).where(contractEntity.startSiteId.id.eq(siteId)));
            }else if(siteTypeCode.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {
                return waybillEntity.contract.id.in(jpaQueryFactory.select(contractEntity.id).from(contractEntity).where(contractEntity.endSiteId.id.eq(siteId)));
            }else {
                throw new BadRequestException("현장관리자 구분 정보를 확인 할 수 없습니다");
            }
        }
        return null;
    }
    private BooleanExpression searchMaterialType(String materialTypeCode){
        return materialTypeCode == null ? null : waybillEntity.materialTypeCode.eq(materialTypeCode);
    }
    private BooleanExpression searchStartSite(String startSiteName){
        return startSiteName == null ? null : waybillEntity.startSiteName.contains(startSiteName);
    }
    private BooleanExpression searchEndSite(String endSiteName){
        return endSiteName == null ? null : waybillEntity.endSiteName.contains(endSiteName);
    }

}