package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.dto.StatisticsAdminResponse;
import kr.movements.smv2.dto.StatisticsMaterialDto;
import kr.movements.smv2.dto.StatisticsSiteResponse;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.ContractRepository;
import kr.movements.smv2.repository.SiteManagerRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import kr.movements.smv2.repository.WaybillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.movements.smv2.entity.QWaybillEntity.waybillEntity;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService{
    private final ContractRepository contractRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final WaybillRepository waybillRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public StatisticsAdminResponse adminStatistics(boolean totalSearch, LocalDate date) {

        LocalDateTime startDate = LocalDateTime.of(1900,01,01,01,01,01,111);
        LocalDateTime endDate = LocalDateTime.now();

        if(!totalSearch && date != null) {
            startDate = date.withDayOfMonth(1).atStartOfDay(); //검색할 달의 1일
            endDate = LocalDateTime.of(date.withDayOfMonth(date.lengthOfMonth()), LocalTime.MAX); //검색할 달의 마지막날
        }else if(!totalSearch && date == null) {
            throw new BadRequestException("검색조건을 확인해주세요");
        }


        //검색한 달 데이터
        Long startSiteCount = siteManagerRepository.countBySiteTypeCodeAndCreatedDateBetween(CommonCode.SITE_TYPE_START_SITE.getCode(), startDate, endDate); //상차지 수
        Long endSiteCount = siteManagerRepository.countBySiteTypeCodeAndCreatedDateBetween(CommonCode.SITE_TYPE_END_SITE.getCode(), startDate, endDate); //하차지 수
        Long contractCount = contractRepository.countByCreatedDateBetween(startDate, endDate); //계약 수
        Long waybillCount = waybillRepository.countByCreatedDateBetween(startDate, endDate); //송장 수

        //이전 달 데이터
        Long preStartSiteCount = siteManagerRepository.countBySiteTypeCodeAndCreatedDateBetween("2010", startDate.minusMonths(1), endDate.minusMonths(1)); //상차지 수
        Long preEndSiteCount = siteManagerRepository.countBySiteTypeCodeAndCreatedDateBetween("2020", startDate.minusMonths(1), endDate.minusMonths(1)); //하차지 수
        Long preContractCount = contractRepository.countByCreatedDateBetween(startDate.minusMonths(1), endDate.minusMonths(1)); //계약 수
        Long preWaybillCount = waybillRepository.countByCreatedDateBetween(startDate.minusMonths(1), endDate.minusMonths(1)); //송장 수

        //전달 대비 증가 수 계산
        int startSiteIncreaseCount = (int) (startSiteCount - preStartSiteCount);
        int endSiteIncreaseCount = (int) (endSiteCount - preEndSiteCount);
        int contractIncreaseCount = (int) (contractCount - preContractCount);
        int waybillIncreaseCount = (int) (waybillCount - preWaybillCount);

        //증가 비율 계산 {(현재달 - 이전달)/이전달}*100
        int startSiteIncreaseRate = preStartSiteCount == 0 ? 100 : (int) (( (double) startSiteIncreaseCount / (double) preStartSiteCount) * 100);
        int endSiteIncreaseRate = preEndSiteCount == 0 ? 100 : (int) (( (double) endSiteIncreaseCount / (double) preEndSiteCount) * 100);
        int contractIncreaseRate = preContractCount == 0 ? 100 : (int) (( (double) contractIncreaseCount / (double) preContractCount) * 100);
        int waybillIncreaseRate = preWaybillCount == 0 ? 100 : (int) (( (double) waybillIncreaseCount / (double) preWaybillCount) * 100);

        //지난달도 0 이번달도 0 이면 증가율은 0이다.
        if(startSiteIncreaseRate == 100) {
            startSiteIncreaseRate = startSiteCount == 0 ? 0 : 100;
        }
        if(endSiteIncreaseRate == 100) {
            endSiteIncreaseRate = endSiteCount == 0 ? 0 : 100;
        }
        if(contractIncreaseRate == 100) {
            contractIncreaseRate = contractCount == 0 ? 0 : 100;
        }
        if(waybillIncreaseRate == 100) {
            waybillIncreaseRate = waybillCount == 0 ? 0 : 100;
        }

        Map<String, Integer> materialMap = new HashMap<>();
        materialMap.put(CommonCode.MATERIAL_TOSA.getCode(), waybillRepository.sumByTosaLocalDate(null, null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_STONE.getCode(), waybillRepository.sumByStoneLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_SAND.getCode(), waybillRepository.sumBySandLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_PEBBLE.getCode(), waybillRepository.sumByPebbleLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_CRASHED_STONE.getCode(), waybillRepository.sumByCrashedStoneLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_STONE_POWDER.getCode(), waybillRepository.sumByStonePowderLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_MIXED_AGGREGATE.getCode(), waybillRepository.sumByMixedAggregateLocalDate(null,null, startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_RUBBLE.getCode(), waybillRepository.sumByRubbleLocalDate(null,null, startDate, endDate));

        return StatisticsAdminResponse.builder()
                .startSiteCount(startSiteCount.intValue())
                .startSiteIncreaseRate(startSiteIncreaseRate)
                .startSiteIncreaseCount(startSiteIncreaseCount)
                .endSiteCount(endSiteCount.intValue())
                .endSiteIncreaseRate(endSiteIncreaseRate)
                .endSiteIncreaseCount(endSiteIncreaseCount)
                .contractCount(contractCount.intValue())
                .contractIncreaseRate(contractIncreaseRate)
                .contractIncreaseCount(contractIncreaseCount)
                .waybillCount(waybillCount.intValue())
                .waybillIncreaseRate(waybillIncreaseRate)
                .waybillIncreaseCount(waybillIncreaseCount)
                .materials(materialMap)
                .build();
    }

    @Override
    public StatisticsSiteResponse siteStatistics(Long userId, boolean totalSearch, LocalDate date) {

        LocalDateTime startDate = LocalDateTime.of(1900,01,01,01,01,01,111);
        LocalDateTime endDate = LocalDateTime.now();

        if(!totalSearch && date != null) {
            startDate = date.withDayOfMonth(1).atStartOfDay(); //검색할 달의 1일
            endDate = LocalDateTime.of(date.withDayOfMonth(date.lengthOfMonth()), LocalTime.MAX); //검색할 달의 마지막날
        }else if(!totalSearch && date == null) {
            throw new BadRequestException("검색조건을 확인해주세요");
        }

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));;

        Long siteCount = 0L; //총 계약 수
        List<ContractEntity> contractList; //송장을 조회하기 위한 계약서 list
        int waybillCount = 0; //총 송장 수
        Long preSiteCount = 0L; //이전 달 계약 수
        Long preWaybillCount = 0L; //이전 달 송장 수

        if (siteManagerEntity.getSiteTypeCode().equals(CommonCode.SITE_TYPE_START_SITE.getCode())) { //로그인 계정이 상차지인 경우
            siteCount = contractRepository.countByStartSiteIdAndHasDeletedAndCreatedDateBetween(siteManagerEntity, false, startDate, endDate);
            contractList = contractRepository.findAllByStartSiteIdAndHasDeleted(siteManagerEntity, false);
            preSiteCount = contractRepository.countByStartSiteIdAndHasDeletedAndCreatedDateBetween(siteManagerEntity, false, startDate.minusMonths(1), endDate.minusMonths(1));
        } else { //하차지인 경우
            siteCount = contractRepository.countByEndSiteIdAndHasDeletedAndCreatedDateBetween(siteManagerEntity, false, startDate, endDate);
            contractList = contractRepository.findAllByEndSiteIdAndHasDeleted(siteManagerEntity, false);
            preSiteCount = contractRepository.countByEndSiteIdAndHasDeletedAndCreatedDateBetween(siteManagerEntity, false, startDate.minusMonths(1), endDate.minusMonths(1));
        }

        //contractId로 해당하는 송장갯수 sum
        for(ContractEntity contract : contractList) {
            waybillCount += waybillRepository.countByContractAndHasDeletedAndCreatedDateBetween(contract, false, startDate, endDate);
            preWaybillCount += waybillRepository.countByContractAndHasDeletedAndCreatedDateBetween(contract, false, startDate.minusMonths(1), endDate.minusMonths(1));
        }

        //전달 대비 증가 수 계산
        int contractIncreaseCount = (int) (siteCount - preSiteCount);
        int waybillIncreaseCount = (int) (waybillCount - preWaybillCount);

        //증가 비율 계산 {(현재달 - 이전달)/이전달}*100
        int contractIncreaseRate = preSiteCount == 0 ? 100 : (int) (( (double) contractIncreaseCount / (double) preSiteCount) * 100);
        int waybillIncreaseRate = preWaybillCount == 0 ? 100 : (int) (( (double) waybillIncreaseCount / (double) preWaybillCount) * 100);

        //지난달도 0 이번달도 0 이면 증가율은 0이다.
        if(contractIncreaseRate == 100) {
            contractIncreaseRate = siteCount == 0 ? 0 : 100;
        }
        if(waybillIncreaseRate == 100) {
            waybillIncreaseRate = waybillCount == 0 ? 0 : 100;
        }

        Map<String, Integer> materialMap = new HashMap<>();
        materialMap.put(CommonCode.MATERIAL_TOSA.getCode(), waybillRepository.sumByTosaLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_STONE.getCode(), waybillRepository.sumByStoneLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_SAND.getCode(), waybillRepository.sumBySandLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_PEBBLE.getCode(), waybillRepository.sumByPebbleLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_CRASHED_STONE.getCode(), waybillRepository.sumByCrashedStoneLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_STONE_POWDER.getCode(), waybillRepository.sumByStonePowderLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_MIXED_AGGREGATE.getCode(), waybillRepository.sumByMixedAggregateLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));
        materialMap.put(CommonCode.MATERIAL_RUBBLE.getCode(), waybillRepository.sumByRubbleLocalDate(siteManagerEntity.getId(), siteManagerEntity.getSiteTypeCode(), startDate, endDate));


        return StatisticsSiteResponse.builder()
                .contractCount(siteCount.intValue())
                .contractIncreaseRate(contractIncreaseRate)
                .contractIncreaseCount(contractIncreaseCount)
                .waybillCount(waybillCount)
                .waybillIncreaseRate(waybillIncreaseRate)
                .waybillIncreaseCount(waybillIncreaseCount)
                .materials(materialMap)
                .build();
    }

}
