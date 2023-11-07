package kr.movements.smv2.service;

import com.google.zxing.WriterException;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.SiteManagerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface SiteManagerService {

    boolean siteManagerEmailCheck(String userEmail);

    void siteManagerPwdUpdate(SiteManagerPwdUpdateDto dto);

    List<SiteAreaMapInfo> defaultSiteMapSearch(double longitude1, double latitude2,
                                               double longitude2, double latitude1,String searchType);
    void siteManagerNewQrCode(Long siteId, String certificationPw);

    void siteManagerDelete(Long userId);

    List<SiteAreaMapInfo> contractSiteMapSearch(String keyword, String searchType);

    Page<SiteAreaInfo> siteAddressSearch(Long userId, Pageable pageable, String keyword, String searchType);

    void siteManagerInfoUpdate(Long userId, SiteManagerUpdateDto dto);

    void siteManagerUpdate(Long userId, SiteManagerUpdateDto dto);

    Long siteManagerSave(SiteManagerSaveDto dto);

    Page<SiteManagerList> siteManagerSearchList(Pageable pageable, String searchType, String keyword, String siteTypeCode);

    SiteManagerInfo siteManagerInfo(Long userId);

    ResponseEntity<byte[]> qrCodeExport(Long siteId) throws IOException, WriterException;

    void siteManagerQRCodeDownload(HttpServletResponse response,Long siteId) throws IOException, WriterException;
}