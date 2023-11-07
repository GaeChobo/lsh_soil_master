package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.util.MapUtil;
import kr.movements.smv2.common.util.NaverGeoLocation;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.*;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverWaybillServiceImpl implements DriverWaybillService{

    private final LocationInfoRepository locationInfoRepository;
    private final WaybillRepository waybillRepository;
    private final UserInfoRepository userInfoRepository;
    private final DriverMemberRepository driverMemberRepository;
    private final ReasonRepository reasonRepository;
    private final ContractRepository contractRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final SiteManagerCertificationRepository siteManagerCertificationRepository;
    private final FileRepository fileRepository;
    private final MapUtil mapUtil;
    private final NaverGeoLocation naverGeoLocation;
    private final AlarmRepository alarmRepository;
    private final MaterialRepository materialRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public List<CodeValue> waybillSearchMaterials(Long contractId) {

        List<MaterialEntity> materials = materialRepository.findByContractIdAndHasDeleted(contractId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("등록된 자재종류를 확인 할 수 없습니다"));

        List<CodeValue> codeValueList = new ArrayList<>();
        Map<String, String> materialCodeMap = CommonCode.getMaterialCodeMap();

        for (MaterialEntity material : materials) {
            String materialTypeCode = material.getMaterialCode();
            String materialTypeValue = materialCodeMap.get(materialTypeCode);
            if (materialTypeValue != null) {
                CodeValue codeValue = new CodeValue(materialTypeCode, materialTypeValue);
                codeValueList.add(codeValue);
            }
        }

        return codeValueList;
    }

    @Override
    @Transactional
    public WaybillCompleteResponse waybillComplete(Long waybillId, String certificationPw, Boolean qrcodeUse) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장을 확인할 수 없습니다."));

        // 검수대기 상태인지 확인
        if (!waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {
            throw new BadRequestException("검수 대기 상태가 아닙니다.");
        }

        // 인증번호 검증
        validateCertificationPw(certificationPw, waybillEntity, qrcodeUse);

        // 이동예상시간
        Integer ete = waybillEntity.getEte();

        // 실제소요시간 계산
        long durationMillis = Duration.between(waybillEntity.getDepartureTime(), waybillEntity.getWaitTime()).toMillis();
        int durationSeconds = (int) (durationMillis / 1000);

        //지연여부 판단
        boolean isLate = false;
        if (ete != null) {
            int eteDouble = ete * 2;

            LocalDateTime lateTime = waybillEntity.getDepartureTime().plus(Duration.ofMillis(eteDouble)); //지연판단 기준 시간
            isLate = waybillEntity.getWaitTime().isAfter(lateTime);//검수대기 시간이 지연판단 시간보다 이후라면 지연(true)
        }

        if (!isLate) {
            // 지연이 없는 경우에만 업데이트 수행
            waybillRepository.waybillArriveUpdate(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), LocalDateTime.now(), LocalDateTime.now(), waybillId);
        }
        
        return WaybillCompleteResponse.builder()
                .waybillId(waybillId)
                .delayAt(isLate)
                .ete(ete)
                .durationTime(durationSeconds)
                .build();
    }

    @Override
    public void validateCertificationPw(String certificationPw, WaybillEntity waybillEntity, Boolean qrcodeUse) {

        Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());
        Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);
        SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);
        String originCertificationPw = siteCertification.getSiteCertificationPw();

        if (siteCertification.getSiteCertificationPw() == null) {
            throw new BadRequestException("인증번호가 없습니다.");
        }

        // QR 사용 시 인증번호 매칭
        if (qrcodeUse) {
            LocalDateTime modifiedDate = siteCertification.getModifiedDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String formattedDateTime = modifiedDate.format(formatter);
            String mixed = contactEndSiteId.toString() + modifiedDate.toString();

            if (!mixed.equals(certificationPw)) {
                throw new BadRequestException("QR 코드 인증번호가 일치하지 않습니다.");
            }
        }
        // QR 미사용 시 인증번호 매칭
        else {

            if(!certificationPw.equals(originCertificationPw)) {
                throw new BadRequestException("인증번호가 일치하지 않습니다.");
            }

            /*
            if (!originCertificationPw.equals(certificationPw)) {
                throw new BadRequestException("인증번호가 일치하지 않습니다.");
            }
             */
        }
    }

    @Override
    public List<DriverWayBillListResponse> driverWaybillList(Long userId, String startSiteName, String endSiteName,
                                                             String materialTypeCode, String waybillStatusCode,
                                                             LocalDate startDate, LocalDate endDate) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인 할 수 없습니다"));
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사 정보를 확인 할 수 없습니다"));

        return waybillRepository.findAllDriverWaybillList(driverEntity.getId(), startSiteName, endSiteName, materialTypeCode, waybillStatusCode, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));


        /*
        Long driverId = driverMemberRepository.findDriverId(userId);

        if(driverId == null) {
            throw new BadRequestException("확인할 수 없는 유저정보입니다.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //startDate
        LocalDateTime nowTime = null;

        //endDate
        LocalDateTime sevenDaysAgo =  null;

        if(startDate != null && !startDate.isEmpty()) {

            sevenDaysAgo = LocalDateTime.parse(startDate+ " 00:00:00", formatter);

        }else {

            sevenDaysAgo = LocalDateTime.now().minusDays(7);

        }

        if(endDate != null && !endDate.isEmpty()) {

            nowTime = LocalDateTime.parse(endDate+ " 23:59:59", formatter);

        }else {

            nowTime = LocalDateTime.now();

        }

        List<DriverWayBillListResponse> result = waybillRepository.findDriverWayBillList(driverId, startSiteName, endSiteName, materialTypeCode, waybillStatusCode, sevenDaysAgo, nowTime);

        return  result;

         */
    }

    @Override
    public List<CodeValue> materialCodeList(Long userId) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("유저 정보를 확인할 수 없습니다."));

        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("운송기사 정보를 확인할 수 없습니다."));

        List<WaybillEntity> waybillEntityList = waybillRepository.findAllByDriverIdAndHasDeleted(driverEntity.getId(), Boolean.FALSE);

        if (waybillEntityList.isEmpty()) {
            throw new BadRequestException("운송기사 정보를 확인할 수 없습니다.");
        }

        Map<String, String> materialCodeMap = CommonCode.getMaterialCodeMap();
        Set<String> materialTypeCodes = new HashSet<>();
        List<CodeValue> codeValueList = new ArrayList<>();

        for (WaybillEntity waybillEntity : waybillEntityList) {
            String materialTypeCode = waybillEntity.getMaterialTypeCode();
            if (!materialTypeCodes.contains(materialTypeCode)) {
                String materialTypeValue = materialCodeMap.get(materialTypeCode);
                if (materialTypeValue != null) {
                    CodeValue codeValue = new CodeValue(materialTypeCode, materialTypeValue);
                    codeValueList.add(codeValue);
                }
                materialTypeCodes.add(materialTypeCode);
            }
        }

        return codeValueList;
    }

    @Override
    public List<CodeValue> waybillStatusCodeList() {

        List<CodeValue> statusCodeList = new ArrayList<>();
        Map<String, String> statusCodeMap = CommonCode.getWaybillStatusCodeMap();
        for (Map.Entry<String, String> entry : statusCodeMap.entrySet()) {
            String code = entry.getKey();
            String value = entry.getValue();
            statusCodeList.add(new CodeValue(code, value));
        }
        return statusCodeList;
    }

    @Override
    public List<Map<String, Object>> waybillStartSiteList(Long userId) {

        Long driverId = driverMemberRepository.findDriverId(userId);

        if(driverId == null) {

            throw new BadRequestException("확인할 수 없는 유저정보입니다.");
        }

        return waybillRepository.waybillStartSiteList(driverId);
    }

    @Override
    public List<Map<String, Object>> waybillEndSiteList(Long userId) {


        Long driverId = driverMemberRepository.findDriverId(userId);

        if(driverId == null) {

            throw new BadRequestException("확인할 수 없는 유저정보입니다.");
        }

        return waybillRepository.waybillEndSiteList(driverId);
    }


    @Override
    public void waybillStatusWait(Long waybillId) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("계약서를 확인 할 수 없습니다."));

        if(CommonCode.WAYBILL_STATUS_MOVE.getCode().equals(waybillEntity.getWaybillStatusCode())) {

            waybillRepository.waybillStatusWait(CommonCode.WAYBILL_STATUS_WAIT.getCode(),LocalDateTime.now(), LocalDateTime.now(),waybillId);

        } else {

            throw new BadRequestException("이동중 상태가 아닙니다.");
        }
    }
    @Override
    public void waybillStatusStart(Long waybillId, Double latitude, Double longitude, Boolean gpsAgree) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("송장을 확인 할 수 없습니다."));

        if(CommonCode.WAYBILL_STATUS_CREATE.getCode().equals(waybillEntity.getWaybillStatusCode())) {

            //gps허용 시
            if(gpsAgree) {

                ContractEntity contractEntity = contractRepository.findByIdAndHasDeleted(waybillEntity.getContract().getId(), false).orElseThrow(() -> new BadRequestException("계약서 정보를 확인 할 수 없습니다."));;
                SiteManagerEntity endSite = siteManagerRepository.findById(contractEntity.getEndSiteId().getId()).orElseThrow(() -> new BadRequestException("하차지 정보를 확인 할 수 없습니다."));;;


                //여기에서 startSite Lat Lon 받는걸로 수정하면 되는걸로 보여짐
                Map<String, Object> ngl = naverGeoLocation.getNaverApi(latitude, longitude, endSite.getLatitude(), endSite.getLongitude());
                Object ete = ngl.get("duration");

                waybillRepository.waybillStatusStart(CommonCode.WAYBILL_STATUS_MOVE.getCode(), LocalDateTime.now(), (Integer) ete, LocalDateTime.now(), true,waybillId);
            }
            //gps 미하용
            else{

                waybillRepository.waybillStatusStart(CommonCode.WAYBILL_STATUS_MOVE.getCode(), LocalDateTime.now(), null, LocalDateTime.now(), false ,waybillId);

            }

        } else {

            throw new BadRequestException("예약 대기상태가 아닙니다.");
        }
    }
    @Override
    public Boolean locationInfoSaveAndCheck(Long waybillId, Double lat, Double lon) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장을 확인할 수 없습니다."));

        ContractEntity contractEntity = contractRepository.findById(waybillEntity.getContract().getId())
                .orElseThrow(() -> new BadRequestException("계약을 찾을 수 없습니다."));

        if (!waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_MOVE.getCode())) {
            throw new BadRequestException("송장이 이동중 상태가 아닙니다.");
        }

        if (contractEntity.getEndSiteId() == null) {
            throw new BadRequestException("하차지 정보가 없습니다.");
        }

        SiteManagerEntity siteManagerEntity = siteManagerRepository.findById(contractEntity.getEndSiteId().getId())
                .orElseThrow(() -> new BadRequestException("하차지 위치 정보가 없습니다."));

        // 송장의 가장 최근 위도 경도 값
        waybillLocationDto waybillLocationdto = locationInfoRepository.findByWaybillIdLocation(waybillEntity.getId());

        // 하차지의 위도와 경도
        Double targetLat = siteManagerEntity.getLatitude();
        Double targetLon = siteManagerEntity.getLongitude();

        // 반경 50m
        Double radius50m = 50.0;
        // 반경 500m
        Double radius500m = 500.0;

        double earthRadius = 6371.0;  // 지구 반지름 (단위: km)

        double dLat = Math.toRadians(lat - targetLat);
        double dLon = Math.toRadians(lon - targetLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(targetLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c * 1000;  // 거리 계산 결과 (단위: m)

        boolean isInRadius500m = distance <= radius500m;

        if (isInRadius500m) {
            // 500m 범위 안의 경우에만 검수대기 업데이트
            waybillRepository.waybillStatusWait(CommonCode.WAYBILL_STATUS_WAIT.getCode(), LocalDateTime.now(), LocalDateTime.now(), waybillId);
        }

        if (waybillLocationdto != null) {
            // 최근 위치와 현재 위치의 거리 계산
            double latestLat = waybillLocationdto.getLat();
            double latestLon = waybillLocationdto.getLon();

            double dLatLatest = Math.toRadians(lat - latestLat);
            double dLonLatest = Math.toRadians(lon - latestLon);

            double aLatest = Math.sin(dLatLatest / 2) * Math.sin(dLatLatest / 2)
                    + Math.cos(Math.toRadians(latestLat)) * Math.cos(Math.toRadians(lat))
                    * Math.sin(dLonLatest / 2) * Math.sin(dLonLatest / 2);

            double cLatest = 2 * Math.atan2(Math.sqrt(aLatest), Math.sqrt(1 - aLatest));

            double distanceLatest = earthRadius * cLatest * 1000;

            // 거리 계산 결과 (단위: m)
            boolean isInLatestRadius50m = distanceLatest <= radius50m;

            if (!isInLatestRadius50m) {
                // 위치 정보 저장
                LocationInfoEntity newLocation = LocationInfoEntity.builder()
                        .waybill(waybillEntity)
                        .lat(lat)
                        .lon(lon)
                        .build();

                locationInfoRepository.save(newLocation);
            }

        } else {
            // 위치 정보가 없는 경우, 현재 위치를 저장
            LocationInfoEntity newLocation = LocationInfoEntity.builder()
                    .waybill(waybillEntity)
                    .lat(lat)
                    .lon(lon)
                    .build();

            locationInfoRepository.save(newLocation);
        }

        return isInRadius500m;
    }

    @Override
    public void delayReason(DelaySaveDto delaySaveDto) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(delaySaveDto.getWaybillId(), Boolean.FALSE).orElseThrow(() -> new BadRequestException("송장을 확인할 수 없습니다."));

        if(waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

            //직접입력 시
            if(delaySaveDto.getDelayCode().equals(CommonCode.DELAY_REASON_SELF.getCode())) {

                String reason = delaySaveDto.getReason();

                if (reason.length() < 10 || reason.length() > 200) {
                    throw new BadRequestException("사유는 최소 10자 이상, 최대 200자까지 입력해야 합니다.");
                }

                waybillRepository.waybillArriveUpdate(CommonCode.WAYBILL_STATUS_DELAY.getCode(), LocalDateTime.now(), LocalDateTime.now() ,delaySaveDto.getWaybillId());

                reasonRepository.save(
                        ReasonEntity.builder()
                                .reason(reason)
                                .waybill(waybillEntity)
                                .build());
            }
            //교통사고
            else if(delaySaveDto.getDelayCode().equals(CommonCode.DELAY_REASON_ACCIDENT.getCode())) {

                waybillRepository.waybillArriveUpdate(CommonCode.WAYBILL_STATUS_DELAY.getCode(), LocalDateTime.now(),LocalDateTime.now(),delaySaveDto.getWaybillId());

                reasonRepository.save(
                        ReasonEntity.builder()
                                .reason(CommonCode.DELAY_REASON_ACCIDENT.getValue())
                                .waybill(waybillEntity)
                                .build());
            }
            //휴식
            else if(delaySaveDto.getDelayCode().equals(CommonCode.DELAY_REASON_REST.getCode())) {

                waybillRepository.waybillArriveUpdate(CommonCode.WAYBILL_STATUS_DELAY.getCode(), LocalDateTime.now(),LocalDateTime.now(),delaySaveDto.getWaybillId());

                reasonRepository.save(
                        ReasonEntity.builder()
                                .reason(CommonCode.DELAY_REASON_REST.getValue())
                                .waybill(waybillEntity)
                                .build());
            }
            else {

                throw new BadRequestException("잘못된 지연 사유 코드입니다.");
            }

        }else {

            throw new BadRequestException("검수대기 상태가 아닙니다.");
        }
    }


    @Override
    public Boolean waybillNotPassMatching(String certificationPw, Long waybillId, Boolean qrcodeUse) {

        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장을 확인할 수 없습니다."));

        if (!waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {
            throw new BadRequestException("검수대기 상태가 아닙니다.");
        }

        Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());
        Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);
        SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);
        String originCertificationPw = siteCertification.getSiteCertificationPw();

        if (originCertificationPw == null) {
            throw new BadRequestException("인증번호가 없습니다.");
        }

        if (qrcodeUse) {
            LocalDateTime modifiedDate = siteCertification.getModifiedDate();

            String mixed = contactEndSiteId.toString() + modifiedDate.toString();

            if (!mixed.equals(certificationPw)) {
                throw new BadRequestException("QR코드번호가 일치하지 않습니다.");
            }
        } else {

            if(!certificationPw.equals(originCertificationPw)) {
                throw new BadRequestException("인증번호가 일치하지 않습니다.");
            }
            /*
            if (!originCertificationPw.equals(certificationPw)) {
                throw new BadRequestException("인증 비밀번호가 일치하지 않습니다.");
            }
             */
        }
        return true;
    }

    @Transactional
    public void waybillNotPass(Long waybillId, String reasonText, Long fileId, String notPasscode) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("송장을 확인할 수 없습니다."));

        if (!waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {
            throw new BadRequestException("검수대기 상태가 아닙니다.");
        }

        Optional<FileEntity> fileEntity = fileId != null ? fileRepository.findByIdAndHasDeleted(fileId, false) : Optional.empty();

        if (notPasscode.equals(CommonCode.NOT_PASS_ERROR.getCode()) || notPasscode.equals(CommonCode.NOT_PASS_SELF.getCode())) {
            String reason;
            if (notPasscode.equals(CommonCode.NOT_PASS_ERROR.getCode())) {
                reason = CommonCode.NOT_PASS_ERROR.getValue();
            } else {
                if (reasonText.length() < 10 || reasonText.length() > 200) {
                    throw new BadRequestException("사유는 최소 10자 이상, 최대 200자까지 입력해야 합니다.");
                }
                reason = reasonText;
            }

            ReasonEntity reasonEntity = ReasonEntity.builder()
                    .waybill(waybillEntity)
                    .file(fileEntity.orElse(null))
                    .reason(reason)
                    .build();
            reasonRepository.save(reasonEntity);

            waybillRepository.waybillArriveUpdate(CommonCode.WAYBILL_STATUS_UNPASSED.getCode(), LocalDateTime.now(), LocalDateTime.now(), waybillEntity.getId());

            // 불통과 알림 등록
            alarmRepository.save(new AlarmEntity(
                    CommonCode.ALARM_UNPASSED.getCode(),
                    false,
                    waybillEntity
            ));
        } else {
            throw new BadRequestException("유효하지 않은 불통과 사유코드입니다.");
        }
    }

    /*
    @Override
    public Boolean waybillComplete(Long waybillId, String certificationPw, Boolean qrcodeUse) {

        //Qr사용
        if (qrcodeUse) {

            WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("송장을 확인 할 수 없습니다."));

            //qr 사용, gps 사용
            if (waybillEntity.isGpsAgree()) {

                //검수대기 상태일 때만
                if (waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

                    Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                    Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);

                    SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                    String originCertificationPw = siteCertification.getSiteCertificationPw();

                    LocalDateTime modifiedDate = siteCertification.getModifiedDate();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                    String formattedDateTime = modifiedDate.format(formatter);

                    String mixed = contactEndSiteId.toString() + modifiedDate.toString();

                    //QR인증번호 매칭
                    if (siteCertification.getSiteCertificationPw() != null && mixed.equals(certificationPw)) {

                        Integer ete = waybillEntity.getEte();

                        if (ete == null) {

                            throw new BadRequestException("예상소요시간 값 없음");

                        } else {

                            ete *= 2;

                            LocalDateTime departure_time = waybillEntity.getDepartureTime();

                            LocalDateTime arrivalTime = departure_time.plus(Duration.ofMillis(ete));

                            LocalDateTime now = LocalDateTime.now();
                            boolean isLate = now.isAfter(arrivalTime);

                            if (isLate) {

                                return Boolean.FALSE;
                            }

                            else {

                                waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), waybillId);

                                return Boolean.TRUE;
                            }
                        }
                    } else if (originCertificationPw == null) {

                        throw new BadRequestException("인증번호가 없습니다.");

                    } else {

                        throw new BadRequestException("인증번호가 일치하지 않습니다.");
                    }
                }
                //검수 대기 상태 아닐 때
                else {
                    throw new BadRequestException("검수 대기 상태가 아닙니다.");
                }
            }
            //qr 사용 gps 미사용
            else {

                //검수대기 확인
                if (waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

                    Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                    Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);

                    SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                    String originCertificationPw = siteCertification.getSiteCertificationPw();

                    LocalDateTime modifiedDate = siteCertification.getModifiedDate();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                    String formattedDateTime = modifiedDate.format(formatter);

                    String mixed = contactEndSiteId.toString() + modifiedDate.toString();

                    //인증번호 매칭
                    if (siteCertification.getSiteCertificationPw() != null && mixed.equals(certificationPw)) {


                        waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), waybillId);

                        return Boolean.TRUE;


                    } else if (originCertificationPw == null) {

                        throw new BadRequestException("인증번호가 없습니다.");

                    } else {

                        throw new BadRequestException("인증번호가 일치하지 않습니다.");
                    }
                }
                //검수대기 상태가 아닐 떄
                else {
                    throw new BadRequestException("검수대기 상태가 아닙니다.");
                }
            }
        }
        //qr미사용
        else {

            WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("송장을 확인 할 수 없습니다."));

            //qr 미사용 , gps 사용
            if (waybillEntity.isGpsAgree()) {

                //검수 대기 상태 확인
                if (waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

                    Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                    Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);

                    SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                    String originCertificationPw = siteCertification.getSiteCertificationPw();

                    //인증번호 매칭
                    if (siteCertification.getSiteCertificationPw() != null && originCertificationPw.equals(certificationPw)) {

                        Integer ete = waybillEntity.getEte();

                        if (ete == null) {
                            throw new BadRequestException("예상소요시간 값 없음");
                        } else {

                            ete *= 2;

                            LocalDateTime departure_time = waybillEntity.getDepartureTime();

                            LocalDateTime arrivalTime = departure_time.plus(Duration.ofMillis(ete));

                            LocalDateTime now = LocalDateTime.now();
                            boolean isLate = now.isAfter(arrivalTime);

                            if (isLate) {


                                return Boolean.FALSE;
                            }
                            //하차 처리
                            else {

                                waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), waybillId);

                                return Boolean.TRUE;
                            }

                        }

                    } else if (originCertificationPw == null) {

                        throw new BadRequestException("인증번호가 없습니다.");

                    } else {

                        throw new BadRequestException("인증번호가 일치하지않습니다..");
                    }


                }
                else {
                    throw new BadRequestException("검수대기 상태가 아닙니다.");
                }

            }
            //qr미사용 , gps 미사용
            else{

                //검수 대기상태 확인
                if (waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

                    Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                    Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);

                    SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                    String originCertificationPw = siteCertification.getSiteCertificationPw();

                    //비밀번호 매칭
                    if (siteCertification.getSiteCertificationPw() != null && originCertificationPw.equals(certificationPw)) {

                        waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(), waybillId);

                        return Boolean.TRUE;

                    } else if (originCertificationPw == null) {

                        throw new BadRequestException("인증번호가 없습니다.");

                    } else {

                        throw new BadRequestException("인증번호가 일치하지 않습니다.");
                    }

                } else {

                    throw new BadRequestException("검수대기 상태가 아닙니다.");
                }

            }
        }

    }

    @Override
    @Transactional
    public void waybillNotPass(Long waybillId, String certificationPw, String reasonText, Long fileId, Boolean qrcodeUse) {

        //qr 사용시
        if(qrcodeUse) {

            WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("송장을 확인 할 수 없습니다."));

            if(waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {

                Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);


                SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                //조회해서 가져온 인증번호
                String originCertificationPw = siteCertification.getSiteCertificationPw();

                //조회해서 가져온 날짜
                LocalDateTime modifiedDate = siteCertification.getModifiedDate();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                String formattedDateTime = modifiedDate.format(formatter);

                String mixed = contactEndSiteId.toString()+modifiedDate.toString();

                //QR코드 혼합된 비밀번호
                if(certificationPw != null && mixed.equals(certificationPw)) {

                    waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_UNPASSED.getCode(), waybillEntity.getId());

                } else if (originCertificationPw == null){

                    throw new BadRequestException("인증번호가 없습니다.");

                } else {

                    throw new BadRequestException("인증번호가 일치하지 않습니다.");
                }

                Optional<FileEntity> fileEntity = fileRepository.findByIdAndHasDeleted(fileId, false);
                if(fileEntity.isPresent()) {
                    reasonRepository.save(
                            ReasonEntity.builder()
                                    .waybill(waybillEntity)
                                    .file(fileEntity.get())
                                    .reason(reasonText)
                                    .build());
                } else {

                    reasonRepository.save(
                            ReasonEntity.builder()
                                    .waybill(waybillEntity)
                                    .file(null)
                                    .reason(reasonText)
                                    .build());
                }
            }
            else {
                throw new BadRequestException("검수대기 상태가 아닙니다.");
            }



            //qr 미사용
        } else {

            WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("계약서를 확인 할 수 없습니다."));

            if(waybillEntity.getWaybillStatusCode().equals(CommonCode.WAYBILL_STATUS_WAIT.getCode())) {


                Long contractID = waybillRepository.waybillFindContractID(waybillEntity.getId());

                Long contactEndSiteId = contractRepository.findByEndSiteId(contractID);

                SiteCertification siteCertification = siteManagerCertificationRepository.findCertificationPw(contactEndSiteId);

                //가져온 인증번호
                String originCertificationPw = siteCertification.getSiteCertificationPw();


                //비밀번호 매칭
                if(originCertificationPw != null && originCertificationPw.equals(certificationPw)) {

                    waybillRepository.waybillStatusUpdate(CommonCode.WAYBILL_STATUS_UNPASSED.getCode(), waybillEntity.getId());

                } else if (originCertificationPw == null){

                    throw new BadRequestException("인증번호가 없습니다.");

                } else {

                    throw new BadRequestException("인증번호가 일치하지 않습니다.");
                }

                Optional<FileEntity> fileEntity = fileRepository.findByIdAndHasDeleted(fileId, false);
                if(fileEntity.isPresent()) {
                    reasonRepository.save(
                            ReasonEntity.builder()
                                    .waybill(waybillEntity)
                                    .file(fileEntity.get())
                                    .reason(reasonText)
                                    .build());
                }else {

                    reasonRepository.save(
                            ReasonEntity.builder()
                                    .waybill(waybillEntity)
                                    .file(null)
                                    .reason(reasonText)
                                    .build());
                }

            }
            else{

                throw new BadRequestException("검수대기 상태가 아닙니다.");
            }

        }


    }

    */

    @Override
    @Transactional
    public void realTimeLatLonSave(Long waybillId, Long userId, Double lat, Double lon) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자정보를 확인할 수 없습니다."));;
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사 정보를 확인할 수 없습니다."));;
        WaybillEntity waybillEntity = waybillRepository.findByIdAndDriverAndHasDeleted(waybillId, driverEntity, false).orElseThrow(() -> new BadRequestException("송장정보를 확인할 수 없습니다."));
        locationInfoRepository.save(new LocationInfoEntity().builder()
                        .waybill(waybillEntity)
//                        .lat(BigDecimal.valueOf(lat))
                        .lat(lat)
//                        .lon(BigDecimal.valueOf(lon))
                        .lon(lon)
                        .build());
    }

    @Override
    public List<DriverWaybillSearchStartSiteResponse> waybillSearchStartSite(Double lat, Double lon, String keyword) {
        List<DriverWaybillSearchStartSiteResponse> list = contractRepository.waybillSearchStartSite(keyword);

        double lat1;
        double lon1;
//        list.stream().map(location -> location.setDistance(mapUtil.distance(lat, lon, location.getLat(), location.getLon(), "kilometer"))).collect(Collectors.toCollection())
        for (DriverWaybillSearchStartSiteResponse location : list) {
            lat1 = location.getLat();
            lon1 = location.getLon();
            //FIXME : 소수점 정리 필요. 주해님과 테스트 해야할 듯. 올림으로 적용중
            location.setDistance(Math.ceil(mapUtil.distance(lat, lon, lat1, lon1, "meter"))); //현위치와 검색된 상차지간 거리 측정
        }
        Collections.sort( list, (o1, o2) -> (int) (o1.getDistance() - o2.getDistance()));//가까운순으로 정렬

        return list;
    }

    @Override
    public List<DriverWaybillSearchEndSiteResponse> waybillSearchEndSite(Long startSiteId) {
        return contractRepository.waybillSearchEndSite(startSiteId);
    }

    @Override
    @Transactional
    public void waybillGpsUpdate(Long waybillId) {
        WaybillEntity waybillEntity = waybillRepository.findByIdAndHasDeleted(waybillId, false).orElseThrow(() -> new BadRequestException("송장정보를 확인할 수 없습니다."));

        waybillEntity.gpsUpdate(true);
    }
/*
    @Override
    public List<DriverWaybillSearchMaterialResponse> waybillSearchMaterials(Long startSiteId, Long endSiteId) {
//        ContractEntity contractEntity = contractRepository.findByStartSiteIdAndEndSiteId()
        return null;
    }

 */

}
