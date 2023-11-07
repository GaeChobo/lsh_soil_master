package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.NotFoundException;
import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.util.MapUtil;
import kr.movements.smv2.common.util.RegexUtil;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.*;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaybillServiceImpl implements WaybillService{

    private final WaybillRepository waybillRepository;
    private final ContractRepository contractRepository;
    private final MaterialRepository materialRepository;
    private final ReasonRepository reasonRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final DriverMemberRepository driverMemberRepository;
    private final UserInfoRepository userInfoRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final SiteManagerCertificationRepository siteManagerCertificationRepository;
    private final LocationInfoRepository locationInfoRepository;

    private final MapUtil mapUtil;
    private final RegexUtil regexUtil;

    @Override
    public TransportUnprocessedDetailDto transportUnprocessedDetail (Long waybillId) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        return waybillRepository.getTransportUnprocessedDetail(waybillEntity.getId());
    }

    @Override
    public List<SiteAreaMapInfo> waybillSiteMapSearch(String searchType, String keyword, Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 현장정보입니다."));
//        if (keyword != null && keyword.trim().length() < 2) {
//            throw new BadRequestException("검색어는 두글자 이상으로 입력해주세요.");
//        }

        if(keyword == null) {
            keyword = "";
        }

        //상차지 검색 시
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteManagerRepository.waybillSiteMapSearch(CommonCode.SITE_TYPE_START_SITE.getCode(), keyword, siteManagerEntity.getId());

        } else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {

            return siteManagerRepository.waybillSiteMapSearch(CommonCode.SITE_TYPE_END_SITE.getCode(), keyword, siteManagerEntity.getId());

        }else {

            throw new BadRequestException("현장 구분코드를 넣어주세요");
        }
    }


    @Override
    public List<SiteAreaMapInfo> waybillMapList(String searchType, Long userId, String keyword) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 현장정보입니다."));
        //상차지 검색 시

        if(keyword == null) {
            keyword = "";
        }
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteManagerRepository.waybillStartSiteMapList(siteManagerEntity.getId(), keyword);

        } else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {

            return siteManagerRepository.waybillEndSiteMapList(siteManagerEntity.getId(), keyword);
            
        }else {

            throw new BadRequestException("현장 구분코드를 넣어주세요");
        }
    }

    @Override
    public Page<SiteAreaAddressInfo> waybillAddressSearch(String searchType ,String keyword, Long userId, Pageable pageable) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));

        if(keyword == null) {
            keyword = "";
        }
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteManagerRepository.waybillStartSiteAddressList(siteManagerEntity.getLongitude(), siteManagerEntity.getLatitude(), siteManagerEntity.getId(), keyword, pageable);
        } else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {

            return siteManagerRepository.waybillEndSiteAddressList(siteManagerEntity.getLongitude(), siteManagerEntity.getLatitude(), siteManagerEntity.getId(), keyword , pageable);
        }else {

            throw new BadRequestException("현장 구분코드를 넣어주세요");
        }

        /*
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 현장정보입니다."));
        //        if (keyword != null && keyword.trim().length() < 2) {
//            throw new BadRequestException("검색어는 두글자 이상으로 입력해주세요.");
//        }

        if(keyword == null) {
            keyword = "";
        }
        if(searchType == null) {

            throw new NotFoundException("상하차지 구분이 필요합니다.1");
        }

        //상차지
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteManagerRepository.waybillAddressSearch("2010", keyword, siteManagerEntity.getId(),pageable);
        }
        //하차지
        else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())){

            return siteManagerRepository.waybillAddressSearch("2020", keyword, siteManagerEntity.getId(),pageable);

        } else {

            throw new NotFoundException("상하차지 구분이 필요합니다.");
        }

         */
    }


    @Override
    public List<ContractMaterialResponse> contractMaterial(Long startSiteId, Long endSiteId) {

        Long contractId = contractRepository.findByContractId(startSiteId,endSiteId);

        if(contractId == null) {

            throw new BadRequestException("계약서 정보를 확인 할 수 없습니다");

        } else {

            //ContractEntity contractEntity = contractRepository.findById(contractId).orElseThrow(() -> new BadRequestException("계약서를 확인 할 수 없습니다."));

            List<String> result = materialRepository.ContractmaterialList(contractId);

            List<CommonCode> resultList = CommonCode.findByCodeList(result);

            List<Map<String, String>> resultMapList = new ArrayList<>();

            for (CommonCode example : resultList) {
                String value = example.getValue();

                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", example.getCode());
                resultMap.put("value", value);

                resultMapList.add(resultMap);
            }

            List<ContractMaterialResponse> responseList = new ArrayList<>();

            for (Map<String, String> resultMap : resultMapList) {
                responseList.add(ContractMaterialResponse.fromMap(resultMap));
            }

            if(responseList.isEmpty()) {
                throw new BadRequestException("데이터가 없습니다.");
            }

            return responseList;
        }
    }

    @Override
    public Page<WaybillListDto> adminWaybillList(Long userId, String searchType, String keyword, String waybillStatus, Pageable pageable) {

        //admin 계정인지 확인
        userInfoRepository.findByIdAndUserAuthCodeAndHasDeleted(userId, "1010", false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));

        return waybillRepository.findAllAdminWaybillList(searchType, keyword, waybillStatus, pageable);
    }

    @Override
    public WaybillInfoDto adminWaybillInfo(WaybillSaveDto waybillSaveDto) {
        return null;
    }

    @Override
    @Transactional
    public void adminWaybillDelete(WaybillSaveDto waybillSaveDto) {

    }

    @Override
    public Page<WaybillListDto> waybillList(Long userId, String searchType, String content, String waybillStatus, Pageable pageable) {

        //송장상태 필터값 체크
        if(StringUtils.hasText(waybillStatus)) {
            CommonCode.codeCheck(waybillStatus); //없을시 badRequest
        }

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));;

        Long siteManagerId = siteManagerEntity.getId(); //현장관리자 id

        String siteType = siteManagerEntity.getSiteTypeCode(); //현장구분(상차지,하차지)

        return waybillRepository.findAllWaybillList(siteManagerId, siteType, searchType, content, waybillStatus, pageable);
    }

    @Override
    @Transactional
    public Long waybillCreate(WaybillSaveDto dto, Long fileId) {

        CommonCode.codeCheck(dto.getWaybillStatusCode()); //송장상태 코드 검사
        CommonCode.codeCheck(dto.getMaterialTypeCode());  //자재종류 코드 검사

        Optional<SiteManagerEntity> startSite = siteManagerRepository.findById(dto.getStartSiteId()); //계약서 상차지 조회
        Optional<SiteManagerEntity> endSite = siteManagerRepository.findById(dto.getEndSiteId()); //계약서 하차지 조회

        if( !(startSite.isPresent() && endSite.isPresent()) ) {
            throw new BadRequestException("현장정보를 확인 할 수 없습니다");
        }

        ContractEntity contract = contractRepository.findByStartSiteIdAndEndSiteId(startSite.get(), endSite.get()).orElseThrow(() -> new BadRequestException("계약서 정보를 확인 할 수 없습니다"));
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(dto.getUserId(), false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인 할 수 없습니다"));;
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity,false).orElseThrow(() -> new BadRequestException("운송기사 정보를 확인 할 수 없습니다"));;

        //계약서 정보로 송장번호 부여할 값 확인
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
        String waybillNum = LocalDateTime.now().format(formatters).toString();

        if(!dto.isWaybillNumAuto()) { //송장번호 수동 입력 시
            if(waybillRepository.existsByContractAndWaybillNum(contract, dto.getWaybillNum())) { //계약서에 등록된 송장번호가 있음
                throw new BadRequestException("송장번호가 중복됩니다");
            }
            waybillNum = dto.getWaybillNum();
            regexUtil.waybillNumCheck(waybillNum); //송장번호 규칙 확인
        }

        //출발일시가 도착일시보다 먼저인지 체크
        if(dto.getDepartureTime().isAfter(dto.getArriveTime())) {
            throw new BadRequestException("도착일시가 출발일시보다 먼저일 수 없습니다");
        }

        WaybillEntity waybillEntity = waybillRepository.save(WaybillEntity.builder()
                .waybillNum(waybillNum)
                .transportVolume(dto.getTransportVolume())
                .carNumber(dto.getCarNumber())
                .departureTime(dto.getDepartureTime())
                .waitTime(dto.getArriveTime()) //사후처리이기 때문에 검수대기는 도착시간과 동일하게 등록
                .arriveTime(dto.getArriveTime())
                .waybillStatusCode(dto.getWaybillStatusCode())
                .ete(0)
                .startSiteName(startSite.get().getSiteName())
                .endSiteName(endSite.get().getSiteName())
                .materialTypeCode(dto.getMaterialTypeCode())
                .contract(contract)
                .driver(driverEntity)
                .gpsAgree(dto.isGpsAgree())
                .build());

        //지연통과, 불통과인 경우 사유등록
        String reason;
        if(dto.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_DELAY.getCode()) || dto.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_UNPASSED.getCode())) {
            reason = CommonCode.valueOfCode(dto.getReasonTypeCode());
            //직접입력인 경우 dto의 reason을 적용
            if(dto.getReasonTypeCode().equals(CommonCode.DELAY_REASON_SELF.getCode()) || dto.getReasonTypeCode().equals(CommonCode.NOT_PASS_SELF.getCode())) {
                reason = dto.getReason();
            }
            //불통과 & 이미지 있을경우
            FileEntity fileEntity = null;
            if(dto.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()) && fileId != null) {
                fileEntity = fileRepository.findByIdAndHasDeleted(fileId, false).orElseThrow(() -> new BadRequestException("이미지 파일을 찾을 수 없습니다"));
            }
            reasonRepository.save(ReasonEntity.builder()
                    .reason(reason)
                    .waybill(waybillEntity)
                    .file(fileEntity)
                    .build());
        }

        return waybillEntity.getId();
    }

    private Integer waybillNumCreate() {
        int result = 1;

        return result;
    }


    @Override
    public WaybillInfoDto waybillInfo(Long waybillId) throws IOException {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, false).orElseThrow(() -> new BadRequestException("송장정보를 확인 할 수 없습니다"));
        WaybillInfoDto.WaybillInfoDtoBuilder waybillInfoDto = WaybillInfoDto.builder();

        //송장 상태에 따라 사유는 있을수도 있고 없을수도 있음
        if(waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_DELAY.getCode()) || waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_UNPASSED.getCode())) { //송장이 지연하차, 불통과인 경우
            Optional<ReasonEntity> reasonEntity = reasonRepository.findByWaybillAndHasDeleted(waybillEntity, false);

            //프로세스상 사유는 무조건 존재해야하지만 없을경우 Null로 처리.
            waybillInfoDto.reason(null); //없을경우 사유 = ""

            if(reasonEntity.isPresent()) {
                waybillInfoDto.reason(reasonEntity.get().getReason());
                if(reasonEntity.get().getFile() != null) {//image가 있다면
                    byte[] thumbnail = fileService.getThumbnail(reasonEntity.get().getFile().getId());
                    waybillInfoDto.thumbnail(thumbnail)
                            .fileId(reasonEntity.get().getFile().getId());
                }
            }
        }

        String departureTime = null; //출발시간
        String arriveTime = null; //도착시간
        String waitTime = null; //검수대기시간

        //출발시간 값이 null인 경우 에러발생하여 처리
        if (waybillEntity.getDepartureTime() != null) {
            departureTime = waybillEntity.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        //도착시간 값이 null인 경우 에러발생하여 처리
        if (waybillEntity.getArriveTime() != null) {
            arriveTime = waybillEntity.getArriveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        //검수대기 시간값이 null인 경우 에러발생하여 처리
        if (waybillEntity.getWaitTime() != null) {
            waitTime = waybillEntity.getWaitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        waybillInfoDto.waybillId(waybillEntity.getId())
                .waybillStatusCode(waybillEntity.getWaybillStatusCode())
                .waybillStatus(CommonCode.valueOfCode(waybillEntity.getWaybillStatusCode()))
                .waybillNum(waybillEntity.getWaybillNum())
                .startSiteName(waybillEntity.getStartSiteName())
                .endSiteName(waybillEntity.getEndSiteName())
                .materialTypeCode(waybillEntity.getMaterialTypeCode())
                .materialType(CommonCode.valueOfCode(waybillEntity.getMaterialTypeCode()))
                .transportVolume(waybillEntity.getTransportVolume())
                .userId(waybillEntity.getDriver().getUserInfoId().getId())
                .driverName(waybillEntity.getDriver().getUserInfoId().getUserName())
                .carNumber(waybillEntity.getCarNumber())
                .gpsAgree(waybillEntity.isGpsAgree())
                .departureTime(departureTime)
                .arriveTime(arriveTime)
                .waitTime(waitTime);

        return waybillInfoDto.build();
    }

    @Override
    @Transactional
    public void waybillUpdate(WaybillSaveDto waybillSaveDto) {

    }


    @Override
    @Transactional
    public void webWaybillDeleted(Long waybillId, String reason, String reasonType) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장정보를 확인 할 수 없습니다"));

        String status = waybillEntity.getWaybillStatusCode();
        boolean isStatusValid = status.equals(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                || status.equals(CommonCode.WAYBILL_STATUS_MOVE.getCode())
                || status.equals(CommonCode.WAYBILL_STATUS_WAIT.getCode());

        if (isStatusValid) {
            if (status.equals(CommonCode.WAYBILL_STATUS_CREATE.getCode())) {
                waybillEntity.setHasDeleted(true);
            }
            else {
                boolean isReasonValid = reasonType.equals(CommonCode.QUIT_REASON_ACCIDENT.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_WEATHER.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_SITE_REQUEST.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_SELF.getCode());

                if (isReasonValid) {
                    String reasonValue = null;

                    if (reasonType.equals(CommonCode.QUIT_REASON_ACCIDENT.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_ACCIDENT.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_WEATHER.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_WEATHER.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_SITE_REQUEST.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_SITE_REQUEST.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_SELF.getCode())) {
                        reasonValue = reason;
                    }

                    ReasonEntity reasonEntity = ReasonEntity.builder()
                            .waybill(waybillEntity)
                            .reason(reasonValue)
                            .build();
                    reasonRepository.save(reasonEntity);
                    waybillEntity.updateUnprocessed(CommonCode.WAYBILL_STATUS_QUIT.getCode());
                } else {
                    throw new BadRequestException("유효한 강제종료 사유코드가 아닙니다.");
                }
            }
        } else {
            throw new BadRequestException("송장 삭제불가");
        }
    }

    @Override
    @Transactional
    public void driverWaybillDelete(Long waybillId, Long fileId, String reason, String reasonType) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장정보를 확인 할 수 없습니다"));

        String status = waybillEntity.getWaybillStatusCode();
        boolean isStatusValid = status.equals(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                || status.equals(CommonCode.WAYBILL_STATUS_MOVE.getCode())
                || status.equals(CommonCode.WAYBILL_STATUS_WAIT.getCode());

        if (isStatusValid) {
            if (status.equals(CommonCode.WAYBILL_STATUS_CREATE.getCode())) {
                waybillEntity.setHasDeleted(true);
            }
            else {
                boolean isReasonValid = reasonType.equals(CommonCode.QUIT_REASON_ACCIDENT.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_WEATHER.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_SITE_REQUEST.getCode())
                        || reasonType.equals(CommonCode.QUIT_REASON_SELF.getCode());

                if (isReasonValid) {
                    String reasonValue = null;

                    if (reasonType.equals(CommonCode.QUIT_REASON_ACCIDENT.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_ACCIDENT.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_WEATHER.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_WEATHER.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_SITE_REQUEST.getCode())) {
                        reasonValue = CommonCode.QUIT_REASON_SITE_REQUEST.getValue();
                    } else if (reasonType.equals(CommonCode.QUIT_REASON_SELF.getCode())) {
                        reasonValue = reason;
                    }

                    ReasonEntity reasonEntity = ReasonEntity.builder()
                            .waybill(waybillEntity)
                            .file(fileId != null ? fileRepository.findByIdAndHasDeleted(fileId, false).orElse(null) : null)
                            .reason(reasonValue)
                            .build();
                    reasonRepository.save(reasonEntity);
                    waybillEntity.updateUnprocessed(CommonCode.WAYBILL_STATUS_QUIT.getCode());
                } else {
                    throw new BadRequestException("유효한 강제종료 사유코드가 아닙니다.");
                }
            }
        } else {
            throw new BadRequestException("송장 삭제불가");
        }
    }

    @Override
    @Transactional
    public void waybillDelete(Long waybillId) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, false).orElseThrow(() -> new BadRequestException("송장정보를 확인 할 수 없습니다"));
        String status = waybillEntity.getWaybillStatusCode();
        String result = "";
        if(status.equals(CommonCode.WAYBILL_STATUS_CREATE.getCode())) {
            waybillEntity.setHasDeleted(true);
//            result = "삭제완료";
        } else if (status.equals(CommonCode.WAYBILL_STATUS_MOVE.getCode()) || status.equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) { //이동중, 검수대기중인 경우
            waybillEntity.updateUnprocessed(CommonCode.WAYBILL_STATUS_QUIT.getCode());
//            result = "강제 운송종료";
        } else {
            throw new BadRequestException("송장 삭제불가");
        }

//        return result;
    }

    @Override
    public Page<WaybillDriverListDto> waybillDriverList(String searchType, String keyword, Pageable pageable) {
        return driverMemberRepository.findAllSearchDriverList(searchType, keyword, pageable);
    }

    @Override
    public List<WaybillSearchSiteListDto> searchSite(Long userId) {
        List<WaybillSearchSiteListDto> result = new ArrayList<>();
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장정보를 확인 할 수 없습니다"));;

        double lat1 = siteManagerEntity.getLatitude().doubleValue();
        double lon1 = siteManagerEntity.getLongitude().doubleValue();
        double distance;

        if(siteManagerEntity.getSiteTypeCode().equals(CommonCode.SITE_TYPE_START_SITE)) { //로그인 계정이 상차지인 경우
            //계약서 리스트 조회
            List<ContractEntity> contractEntityList = contractRepository.findAllByStartSiteIdAndHasDeleted(siteManagerEntity, false);
            //하차지 id와 계약서 id를 저장
            for(int i = 0; i < contractEntityList.size(); i++) {
                distance = mapUtil.distance(lat1,lon1, contractEntityList.get(i).getEndSiteId().getLatitude().doubleValue(), contractEntityList.get(i).getEndSiteId().getLatitude().doubleValue(), "kilometer");
                result.add(WaybillSearchSiteListDto.builder().siteId(contractEntityList.get(i).getEndSiteId().getId())
                        .siteType("하차지")
                        .siteName(contractEntityList.get(i).getEndSiteId().getSiteName())
                        .address(contractEntityList.get(i).getEndSiteId().getAddress())
                        .addressDetail(contractEntityList.get(i).getEndSiteId().getAddressDetail())
                        .latitude(contractEntityList.get(i).getEndSiteId().getLatitude())
                        .longitude(contractEntityList.get(i).getEndSiteId().getLongitude())
                        .distance(distance)
                        .contractId(contractEntityList.get(i).getId())
                        .build());
            }
        }else if(siteManagerEntity.getSiteTypeCode().equals(CommonCode.SITE_TYPE_END_SITE)) { //로그인 계정이 하차지인 경우
            List<ContractEntity> contractEntityList = contractRepository.findAllByEndSiteIdAndHasDeleted(siteManagerEntity, false);
            //하차지 id와 계약서 id를 저장
            for(int i = 0; i < contractEntityList.size(); i++) {
                distance = mapUtil.distance(lat1,lon1, contractEntityList.get(i).getStartSiteId().getLatitude().doubleValue(), contractEntityList.get(i).getStartSiteId().getLatitude().doubleValue(), "kilometer");
                result.add(WaybillSearchSiteListDto.builder().siteId(contractEntityList.get(i).getEndSiteId().getId())
                        .siteType("상차지")
                        .siteName(contractEntityList.get(i).getEndSiteId().getSiteName())
                        .address(contractEntityList.get(i).getEndSiteId().getAddress())
                        .addressDetail(contractEntityList.get(i).getEndSiteId().getAddressDetail())
                        .latitude(contractEntityList.get(i).getEndSiteId().getLatitude())
                        .longitude(contractEntityList.get(i).getEndSiteId().getLongitude())
                        .distance(distance)
                        .contractId(contractEntityList.get(i).getId())
                        .build());
            }

        }else {
            throw new IllegalArgumentException("");
        }

        //TODO: distance 기준, 가까운 순서로 정렬


        return result;
    }

    @Override
    public Page<TransportUnprocessedListDto> waybillUnprocessedList(Long userId, String searchType, String keyword, Pageable pageable) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));;
        Long siteManagerId = siteManagerEntity.getId(); //현장관리자 id
        String siteType = siteManagerEntity.getSiteTypeCode(); //현장구분(상차지,하차지)

        return waybillRepository.findAllTransportUnprocessedList(siteManagerId, siteType, searchType, keyword, pageable);
    }

    @Override
    @Transactional
    public void driverWaybillCreate(DriverWaybillSaveDto dto) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(dto.getUserId(), false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사 정보를 확인 할 수 없습니다"));
        SiteManagerEntity startSite = siteManagerRepository.findById(dto.getStartSiteId()).orElseThrow(() -> new BadRequestException("상차지 정보를 확인 할 수 없습니다"));
        SiteManagerEntity endSite = siteManagerRepository.findById(dto.getEndSiteId()).orElseThrow(() -> new BadRequestException("하차지 정보를 확인 할 수 없습니다"));
        ContractEntity contractEntity = contractRepository.findByStartSiteIdAndEndSiteId(startSite, endSite).orElseThrow(() -> new BadRequestException("계약서 정보를 확인 할 수 없습니다"));;

        //FIXME : dto에 현장명은 사용하지않음. 제거해도 될듯

        //차량번호 정규식 검사
         regexUtil.carNumCheck(dto.getCarNumber());

         //자재종류 검사
        CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, dto.getMaterialTypeCode());

        //최대 운송량 검사. 최대치는 13루베
        if(dto.getTransportVolume() > 13) {
            throw new BadRequestException("최대 운송량은 13루베 입니다.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
        String waybillNumber = LocalDateTime.now().format(formatter).toString();

        if(!dto.isWaybillNumAuto()) { //송장번호 수동 입력 시
            if(waybillRepository.existsByContractAndWaybillNum(contractEntity, dto.getWaybillNum())) { //계약서에 등록된 번호가 있음
                throw new BadRequestException("송장번호가 중복됩니다");
            }
            waybillNumber = dto.getWaybillNum();
            regexUtil.waybillNumCheck(waybillNumber); //송장번호 규칙 확인
        }

        waybillRepository.save(WaybillEntity.builder()
                        .waybillStatusCode(CommonCode.WAYBILL_STATUS_CREATE.getCode())
                        .waybillNum(waybillNumber)
                        .transportVolume(dto.getTransportVolume())
                        .startSiteName(startSite.getSiteName())
                        .endSiteName(endSite.getSiteName())
                        .materialTypeCode(dto.getMaterialTypeCode())
                        .carNumber(dto.getCarNumber())
                        .contract(contractEntity)
                        .driver(driverEntity)
                        .gpsAgree(dto.isGpaAgree())
                        .build());
    }

    @Override
    public WaybillLatestInfoDto waybillLatestInfo(Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(()-> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        DriverEntity driver = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(()-> new BadRequestException("운송기사정보를 확인 할 수 없습니다"));
        Optional<WaybillEntity> waybillEntity = waybillRepository.findTopByDriverAndHasDeletedOrderByCreatedDateDesc(driver, false);

        //송장등록이 처음인지 확인
        /*
        if(waybillRepository.countByDriver(driver) > 0) { //송장이 있을경우
            Optional<WaybillEntity> waybillEntity = waybillRepository.findTopByDriverOrderByCreatedDateDesc(driver);
            if (waybillEntity.get().isHasDeleted() == true) { //최근송장이 삭제상태인 경우

            }
        }

         */
        if(waybillEntity.isPresent()) {
            WaybillEntity waybill = waybillEntity.get();
            return WaybillLatestInfoDto.builder()
                    .waybillId(waybill.getId())
                    .waybillNum(waybill.getWaybillNum())
                    .waybillStatusCode(waybill.getWaybillStatusCode())
                    .waybillStatus(CommonCode.valueOfCode(waybill.getWaybillStatusCode()))
                    .build();
        }else {
            return WaybillLatestInfoDto.builder()
                    .waybillId(0L)
                    .waybillNum("0")
                    .waybillStatusCode("0")
                    .waybillStatus("송장없음")
                    .build();
        }

    }
/*
    @Override
    public Integer waybillDailyCount(Long userId) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(()-> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(()-> new BadRequestException("운송기사정보를 확인 할 수 없습니다"));

//        LocalDate date = LocalDate.now();
//        LocalDateTime startDate = date.atStartOfDay();
//        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.MAX);

        return Math.toIntExact(waybillRepository.driverDailyUnloadCount(userId));
    }

 */

    @Override
    public DriverWaybillMainResponse driverWaybillMain(Long userId) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다"));
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사정보를 확인 할 수 없습니다"));

        // 최근송장 조회
        Optional<WaybillEntity> waybillEntity = waybillRepository.findTopByDriverAndHasDeletedOrderByCreatedDateDesc(driverEntity, false);

        Long waybillId = null;
        String waybillNum = null;
        String waybillStatusCode = null;
        String startSiteName = "";
        String endSiteName = "";
        Integer ete = null;
        LocalDateTime departureTime = null;

        //첫 운송이 아니면서 마지막 운송이 진행중인 경우
        if(waybillEntity.isPresent() && (waybillEntity.get().getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_CREATE.getCode()) || waybillEntity.get().getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_MOVE.getCode()) || waybillEntity.get().getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode()) )) {
            WaybillEntity waybill = waybillEntity.get();
            waybillId = waybill.getId();
            waybillNum = waybill.getWaybillNum();
            waybillStatusCode = waybill.getWaybillStatusCode();
            startSiteName = waybill.getStartSiteName();
            endSiteName = waybill.getEndSiteName();
            ete = waybill.getEte();
            departureTime = waybill.getDepartureTime();
        }

        //예상소요시간 관련 값 추가 및 지연여부 검증
        if (ete != null) {
            //지연여부 판단 로직
            int eteDouble =  ete * 2;
            LocalDateTime eteDepartureTime = waybillEntity.get().getDepartureTime(); //출발시간
            LocalDateTime arrivalTime = eteDepartureTime.plus(Duration.ofMillis(eteDouble)); //지연판단 기준 시간
            LocalDateTime now = LocalDateTime.now(); //현재시간
            boolean isLate = now.isAfter(arrivalTime); //현재시간이 지연판단 기준 시간보다 이후일때 true(지연)

            return DriverWaybillMainResponse.builder()
                    .delayAt(isLate)
                    .waybillId(waybillId)
                    .waybillNum(waybillNum)
                    .waybillStatusCode(waybillStatusCode)
                    .driverName(userInfoEntity.getUserName())
                    .dailyCount(Math.toIntExact(waybillRepository.driverDailyUnloadCount(userId)))
                    .startSiteName(startSiteName)
                    .endSiteName(endSiteName)
                    .ete(ete)
                    .carNumber(driverEntity.getCarNumber())
                    .departureTime(departureTime)
                    .build();
        } else {

            return DriverWaybillMainResponse.builder()
                    .waybillId(waybillId)
                    .waybillNum(waybillNum)
                    .waybillStatusCode(waybillStatusCode)
                    .driverName(userInfoEntity.getUserName())
                    .dailyCount(Math.toIntExact(waybillRepository.driverDailyUnloadCount(userId)))
                    .startSiteName(startSiteName)
                    .endSiteName(endSiteName)
                    .ete(ete)
                    .delayAt(null)
                    .carNumber(driverEntity.getCarNumber())
                    .departureTime(departureTime)
                    .build();
        }


            /*
        return DriverWaybillMainResponse.builder()
                .waybillId(waybillId)
                .waybillNum(waybillNum)
                .waybillStatusCode(waybillStatusCode)
                .driverName(userInfoEntity.getUserName())
                .dailyCount(Math.toIntExact(waybillRepository.driverDailyUnloadCount(userId)))
                .startSiteName(startSiteName)
                .endSiteName(endSiteName)
                .ete(ete)
                .build();

         */
    }




    @Override
    public Page<TransPortNotPassInfoDto> transportNotPassInfo(Long driverId, Pageable pageable) throws IOException {
        driverMemberRepository.findByIdAndHasDeleted(driverId, false)
                .orElseThrow(() -> new BadRequestException("운송기사 정보를 확인 할 수 없습니다"));

        Page<TransportNotPassInfoResponse> result = waybillRepository.transportNotPassInfo(driverId, pageable);
        List<TransportNotPassInfoResponse> transportNotPassInfoResponseList = result.getContent();

        List<TransPortNotPassInfoDto> updatedList = new ArrayList<>();

        for (TransportNotPassInfoResponse response : transportNotPassInfoResponseList) {

            Long fileId = response.getFileId();

            byte[] thumbnail = null;

            if (fileId != null) {
                thumbnail = fileService.getThumbnail(fileId);
            }

            TransPortNotPassInfoDto dto = TransPortNotPassInfoDto.builder()
                    .waybillId(response.getWaybillId())
                    .waybillNum(response.getWaybillNum())
                    .userId(response.getUserId())
                    .driverId(response.getDriverId())
                    .userName(response.getUserName())
                    .userPhone(response.getUserPhone())
                    .startSiteName(response.getStartSiteName())
                    .endSiteName(response.getEndSiteName())
                    .materialTypeCode(response.getMaterialTypeCode())
                    .materialVolume(response.getMaterialVolume())
                    .departureTime(response.getDepartureTime())
                    .arriveTime(response.getArriveTime())
                    .reason(response.getReason())
                    .thumbnail(thumbnail)
                    .build();

            updatedList.add(dto);
        }

        return new PageImpl<>(updatedList, pageable, result.getTotalElements());
    }
/*
    @Override
    public List<ContractSearchSiteResponse> contractSearchSite(Long userId, String searchType) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));

        //searchType 이 code에 존재하는 값인지 검증
        String searchTypeValue = CommonCode.valueOfCode(searchType);
        List<ContractSearchSiteResponse> result = null;

        //상차지 계정이 하차지 검색시도 혹은 그 반대의 경우만 허용
        if( !((siteManagerEntity.getSiteTypeCode().equals("2010") && searchTypeValue.equals("하차지")) || (siteManagerEntity.getSiteTypeCode().equals("2020") && searchTypeValue.equals("상차지"))) ) {
            throw new BadRequestException("검색항목을 잘못 호출하였습니다. 관리자에게 문의하세요");
        }

//        result = contractRepository.siteWaybillContractSite(siteManagerEntity.getId(), searchTypeValue);
        return result;
    }


 */

}

