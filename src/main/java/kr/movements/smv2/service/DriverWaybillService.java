package kr.movements.smv2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.movements.smv2.dto.CodeValue;
import kr.movements.smv2.dto.DelaySaveDto;
import kr.movements.smv2.dto.DriverWayBillListResponse;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.WaybillEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DriverWaybillService {

    List<CodeValue> waybillSearchMaterials(Long contractId);
    Boolean waybillNotPassMatching(String certificationPw, Long waybillId, Boolean qrcodeUse);

    void validateCertificationPw(String certificationPw, WaybillEntity waybillEntity, Boolean qrcodeUse);

    List<DriverWayBillListResponse> driverWaybillList(Long userId, String startSiteName, String endSiteName,
                                                      String materialTypeCode, String waybillStatusCode,
                                                      LocalDate startDate, LocalDate endDate);
    List<CodeValue> waybillStatusCodeList();
    List<CodeValue> materialCodeList(Long userId);
    List<Map<String, Object>> waybillStartSiteList(Long driverId);

    List<Map<String, Object>> waybillEndSiteList(Long driverId);

    void waybillStatusWait(Long waybillId);
    void waybillStatusStart(Long waybillId, Double latitude, Double longitude, Boolean gpsAgree);
    Boolean locationInfoSaveAndCheck(Long waybillId, Double lat, Double lon);
    void delayReason(DelaySaveDto delaySaveDto);

    WaybillCompleteResponse waybillComplete(Long waybillId, String certificationPw, Boolean qrcodeUse);
    void waybillNotPass(Long waybillId, String reasonText, Long fileId, String notPasscode);
    void realTimeLatLonSave(Long waybillId, Long userId, Double lat, Double lon);

    List<DriverWaybillSearchStartSiteResponse> waybillSearchStartSite(Double lat, Double lon, String keyword);
//    DriverWaybillSearchEndSiteResponse waybillSearchStartSite(DriverWaybillSearchStartSiteDto dto);
    List<DriverWaybillSearchEndSiteResponse> waybillSearchEndSite(Long startSiteId);
//    List<DriverWaybillSearchMaterialResponse> waybillSearchMaterials(Long startSiteId, Long endSiteId);
    void waybillGpsUpdate(Long waybillId);

}
