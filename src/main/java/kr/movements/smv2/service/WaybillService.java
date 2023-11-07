package kr.movements.smv2.service;

import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface WaybillService {

    TransportUnprocessedDetailDto transportUnprocessedDetail (Long waybillId);
    void driverWaybillDelete(Long waybillId, Long fileId, String reason, String reasonType);
    List<SiteAreaMapInfo> waybillSiteMapSearch(String searchType, String keyword, Long userId);

    List<SiteAreaMapInfo> waybillMapList(String searchType, Long userId, String keyword);

    Page<SiteAreaAddressInfo> waybillAddressSearch(String searchType ,String keyword, Long userId, Pageable pageable);

    List<ContractMaterialResponse> contractMaterial(Long startSiteId, Long endSiteId);
    Page<WaybillListDto> adminWaybillList(Long userId, String searchType, String keyword, String waybillStatus, Pageable pageable);
    WaybillInfoDto adminWaybillInfo(WaybillSaveDto waybillSaveDto);
    void adminWaybillDelete(WaybillSaveDto waybillSaveDto);
    Page<WaybillListDto> waybillList(Long userId, String searchType, String keyword, String waybillStatus, Pageable pageable);
    Long waybillCreate(WaybillSaveDto waybillSaveDto, Long fileId);
    WaybillInfoDto waybillInfo(Long waybillId) throws IOException;
    void waybillUpdate(WaybillSaveDto waybillSaveDto);
    void webWaybillDeleted(Long waybillId, String reason, String reasonType);
    void waybillDelete(Long waybillId);
    Page<WaybillDriverListDto> waybillDriverList(String searchType, String keyword, Pageable pageable);
    List<WaybillSearchSiteListDto> searchSite(Long siteManagerId);
    Page<TransportUnprocessedListDto> waybillUnprocessedList(Long userId, String searchType, String keyword, Pageable pageable);

    void driverWaybillCreate(DriverWaybillSaveDto driverWaybillSaveDto);
    WaybillLatestInfoDto waybillLatestInfo(Long userId);
//    Integer waybillDailyCount(Long userId);

//    DriverWaybillMainResponse driverWaybillMain(Long userId, String waybillStatusCode);
    DriverWaybillMainResponse driverWaybillMain(Long userId);

    Page<TransPortNotPassInfoDto> transportNotPassInfo(Long driverId, Pageable pageable) throws IOException;

//    List<ContractSearchSiteResponse> contractSearchSite(Long userId, String searchType);
}
