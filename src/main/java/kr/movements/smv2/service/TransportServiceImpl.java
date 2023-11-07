package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.WaybillEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportServiceImpl implements TransportService{

    private final WaybillRepository waybillRepository;
    private final UserInfoRepository userInfoRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final LocationInfoRepository locationInfoRepository;
    private final ContractRepository contractRepository;
    @Override
    public TransportRealTimeListResponse transportRealTimeList(Long userId, String searchType, String keyword, Pageable pageable) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDateTime startDate = LocalDateTime.parse(LocalDateTime.now().format(formatter));
//        LocalDateTime endDate = LocalDateTime.parse(LocalDateTime.now().format(formatter));

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));;

        Long siteManagerId = siteManagerEntity.getId(); //현장관리자 id
        String siteType = siteManagerEntity.getSiteTypeCode(); //현장구분(상차지,하차지)

        //일일 불통과 건수
        Long failureCount = waybillRepository.findByNotPassCount(siteManagerId, siteType, userId);

        //실시간 운송중인 송장정보
        Page<TransportRealTimeListDto> transportRealTimeList = waybillRepository.findByTransportRealTimeList(siteManagerId, siteType, userId, searchType, keyword, pageable);

        return TransportRealTimeListResponse.builder()
                .content(transportRealTimeList)
                .dailyFailureCount(Math.toIntExact(failureCount))
                .build();
    }

    @Override
    public TransportRealTimeInfoResponse transportRealTimeInfo(Long waybillId) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, false).orElseThrow(() -> new BadRequestException("송장정보를 확인 할 수 없습니다."));
        ContractEntity contractEntity = contractRepository.findById(waybillEntity.getContract().getId()).orElseThrow(() -> new BadRequestException("계약서정보를 확인 할 수 없습니다."));
        SiteManagerEntity startSiteEntity = siteManagerRepository.findById(contractEntity.getStartSiteId().getId()).orElseThrow(() -> new BadRequestException("상차지정보를 확인 할 수 없습니다."));;
        SiteManagerEntity endSiteEntity = siteManagerRepository.findById(contractEntity.getEndSiteId().getId()).orElseThrow(() -> new BadRequestException("하차지정보를 확인 할 수 없습니다."));;
        Optional<List<TransportRealTimeInfoDto>> locationInfo = locationInfoRepository.findWaybillLocations(waybillEntity.getId());

        if(!locationInfo.isPresent()) {
            locationInfo = null;
        }

        return TransportRealTimeInfoResponse.builder()
                .userId(waybillEntity.getDriver().getUserInfoId().getId())
                .contractId(waybillEntity.getContract().getId())
                .startSiteName(waybillEntity.getStartSiteName())
                .startSiteLat(startSiteEntity.getLatitude())
                .startSiteLon(startSiteEntity.getLongitude())
                .endSiteName(waybillEntity.getEndSiteName())
                .endSiteLat(endSiteEntity.getLatitude())
                .endSiteLon(endSiteEntity.getLongitude())
                .materialType(CommonCode.valueOfCode(waybillEntity.getMaterialTypeCode()))
                .transportVolume(waybillEntity.getTransportVolume())
                .locations(locationInfo.get())
                .build();
    }

    @Override
    public Page<TransportDailyNotPassListResponse> transportDailyNotPassList(Long userId, Long siteManagerId, String siteTypeCode, String searchType, String keyword, Pageable pageable) {
        return waybillRepository.findByDailyNotPassList(siteManagerId, siteTypeCode, userId, searchType, keyword, pageable);
    }
}
