package kr.movements.smv2.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.exception.NotFoundException;
import kr.movements.smv2.common.util.CheckUtil;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.SiteManagerCertificationEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.SiteManagerCertificationRepository;
import kr.movements.smv2.repository.SiteManagerRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteManagerServiceImpl implements SiteManagerService{

    private final SiteManagerRepository siteMemberRepository;
    private final UserInfoRepository userInfoRepository;
    private final SiteManagerCertificationRepository siteManagerCertificationRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final CheckUtil checkUtil;

    @Override
    public boolean siteManagerEmailCheck(String userEmail) {
        Long emailCount = userInfoRepository.countByUserEmailAndHasDeleted(userEmail, Boolean.FALSE);
        return emailCount == 0;
    }

    @Override
    public void siteManagerPwdUpdate(SiteManagerPwdUpdateDto dto) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(dto.getUserId(), false)
                .orElseThrow(() -> new BadRequestException("유저정보를 확인 할 수 없습니다."));

        if (!userInfoEntity.getUserTypeCode().equals(CommonCode.AUTH_SITE.getCode())) {
            throw new BadRequestException("현장관리자가 아닙니다.");
        }

        if (!StringUtils.hasText(dto.getOriginPassword())) {
            throw new BadRequestException("비밀번호를 입력해주세요.");
        }

        if (!passwordEncoder.matches(dto.getOriginPassword(), userInfoEntity.getUserPw())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        if (!StringUtils.hasText(dto.getChangedPassword())) {
            throw new BadRequestException("변경할 비밀번호를 입력해주세요.");
        }

        if (!dto.getChangedPassword().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException("8자 이상의 알파벳 문자, 숫자, 특수문자 조합이어야 합니다.");
        }

        if (dto.getChangedPassword().contains(" ")) {
            throw new BadRequestException("공백 입력은 불가능합니다.");
        }

        userInfoRepository.userPwdUpdate(passwordEncoder.encode(dto.getChangedPassword()), userInfoEntity.getId());
    }

    @Override
    @Transactional
    public void siteManagerDelete(Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인할 수 없습니다."));
        userInfoEntity.setHasDeleted(true);

//        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인할 수 없습니다."));;
//        siteManagerEntity.setHasDeleted(true);
    }

    @Override
    public List<SiteAreaMapInfo> contractSiteMapSearch(String searchType, String keyword) {
        keyword = keyword != null ? keyword : "";

        // 검색 유형이 지정되지 않았거나 올바른 상하차지 유형이 아닌 경우 예외
        if (searchType == null || (!searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())
                && !searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode()))) {
            throw new BadRequestException("상하차지 구분이 필요합니다.");
        }

        // 상하차지 유형에 따라 적절한 유형 코드
        String siteTypeCode = searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())
                ? CommonCode.SITE_TYPE_START_SITE.getCode()
                : CommonCode.SITE_TYPE_END_SITE.getCode();

        // 지정된 검색 유형과 키워드로 사이트 지도 정보를 검색한 결과를 반환
        return siteMemberRepository.siteMapSearch(siteTypeCode, keyword, keyword, keyword);
    }

    @Override
    public Page<SiteAreaInfo> siteAddressSearch(Long userId, Pageable pageable, String keyword, String searchType) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("사용자 정보를 확인할 수 없습니다."));

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE)

                .orElseThrow(() -> new BadRequestException("사용자 정보를 확인할 수 없습니다."));
        keyword = keyword != null ? keyword : "";

        if (searchType == null || (!searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())
                && !searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode()))) {
            throw new BadRequestException("상하차지 구분이 필요합니다.");
        }


        String siteTypeCode = searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())
                ? CommonCode.SITE_TYPE_START_SITE.getCode()
                : CommonCode.SITE_TYPE_END_SITE.getCode();

        return siteMemberRepository.siteAddressSearch(siteManagerEntity.getLatitude(), siteManagerEntity.getLongitude(), siteTypeCode, keyword, pageable);
    }


    @Override
    @Transactional
    public void siteManagerInfoUpdate(Long userId, SiteManagerUpdateDto dto) {
        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("회원정보를 확인할 수 없습니다."));
        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, false)
                .orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));
        SiteManagerCertificationEntity siteManagerCertificationEntity = siteManagerCertificationRepository
                .findBySiteIdAndHasDeleted(siteManagerEntity, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("현장관리자 인증정보를 확인 할 수 없습니다."));

        String siteCertificationPw = dto.getSiteCertificationPw();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime modifiedTime = siteManagerCertificationEntity.getModifiedDate();

        String email = dto.getUserEmail();
        String userPhone = dto.getUserPhone();
        String username = dto.getUserName();

        // Set null if empty
        if (!StringUtils.hasText(email)) {
            email = null;
        }
        if (!StringUtils.hasText(userPhone)) {
            userPhone = null;
        }
        if (!StringUtils.hasText(username)) {
            username = null;
        }

        if (email != null) {
            email = checkUtil.emailCheck(email);
            if (StringUtils.containsWhitespace(email)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        }

        if (StringUtils.hasText(siteCertificationPw)) {
            siteCertificationPw = checkUtil.numberCheck(siteCertificationPw);
            if (siteCertificationPw.length() < 4) {
                throw new BadRequestException("비밀번호는 숫자 4자 이상!");
            } else if (StringUtils.containsWhitespace(siteCertificationPw)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        }

        if (userPhone != null) {
            userPhone = checkUtil.phoneNumCheck(userPhone);
            if (StringUtils.containsWhitespace(userPhone)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        }

        if (username != null) {
            if (StringUtils.containsWhitespace(username)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        }

        // 신규 qr
        if (dto.isNewQrCodeUse()) {
            siteMemberRepository.siteManagerInfoUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.userInfoUpdate(username, userPhone, email, userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(siteCertificationPw, currentTime, userId);
        }
        // 신규 qr 사용 안할 때
        else {
            siteMemberRepository.siteManagerInfoUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.userInfoUpdate(username, userPhone, email, userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(siteCertificationPw, modifiedTime, userId);
        }
    }

    @Override
    @Transactional
    public void siteManagerUpdate(Long userId, SiteManagerUpdateDto dto) {
        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("회원정보를 확인할 수 없습니다."));

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, false)
                .orElseThrow(() -> new BadRequestException("현장관리자정보를 확인할 수 없습니다."));

        SiteManagerCertificationEntity siteManagerCertificationEntity = siteManagerCertificationRepository
                .findBySiteIdAndHasDeleted(siteManagerEntity, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("현장관리자 인증정보를 확인할 수 없습니다."));

        String siteCertificationPw = dto.getSiteCertificationPw();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime modifiedTime = siteManagerCertificationEntity.getModifiedDate();

        String email = dto.getUserEmail();
        String userPhone = dto.getUserPhone();
        String userName = dto.getUserName();

        if (StringUtils.hasText(email)) {
            email = checkUtil.emailCheck(email);
            if (StringUtils.containsWhitespace(email)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        } else {
            email = null; // 빈값이면 null로 처리
        }

        if (StringUtils.hasText(siteCertificationPw)) {
            siteCertificationPw = checkUtil.numberCheck(siteCertificationPw);
            if (siteCertificationPw.length() < 4) {
                throw new BadRequestException("비밀번호는 숫자 4자 이상!");
            } else if (StringUtils.containsWhitespace(siteCertificationPw)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        } else {
            siteCertificationPw = null; // 빈값이면 null로 처리
        }

        if (StringUtils.hasText(userPhone)) {
            userPhone = checkUtil.phoneNumCheck(userPhone);
            if (StringUtils.containsWhitespace(userPhone)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        } else {
            userPhone = null; // 빈값이면 null로 처리
        }

        if (StringUtils.hasText(userName)) {
            if (StringUtils.containsWhitespace(userName)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        } else {
            userName = null; // 빈값이면 null로 처리
        }

        // 신규 qr
        if (dto.isNewQrCodeUse()) {
            siteMemberRepository.siteManagerInfoUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.userInfoUpdate(userName, userPhone, email, userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(siteCertificationPw, currentTime, userId);
        }
        // 신규 qr 사용 안할 때
        else {
            siteMemberRepository.siteManagerInfoUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.userInfoUpdate(userName, userPhone, email, userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(siteCertificationPw, modifiedTime, userId);
        }
    }

        /*
        //같을 때
        if(OriginPwd.equals(SiteCertificationPw)) {

            if(StringUtils.hasText(dto.getSiteCertificationPw())) {

                SiteCertificationPw = checkUtil.NumberCheck(dto.getSiteCertificationPw());
            }

            siteMemberRepository.siteManagerUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.UserInfoUpdate(dto.getUserName(),dto.getUserPhone(),dto.getUserEmail(),userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(SiteCertificationPw, modifiedTime ,userId);

        }
        //다를 때
        else {

            if(StringUtils.hasText(dto.getSiteCertificationPw())) {

                SiteCertificationPw = checkUtil.NumberCheck(dto.getSiteCertificationPw());
            }

            siteMemberRepository.siteManagerUpdate(dto.isWeighBridgeType(), userId);
            userInfoRepository.UserInfoUpdate(dto.getUserName(),dto.getUserPhone(),dto.getUserEmail(),userId);
            siteManagerCertificationRepository.siteManagerCertificationUpdate(SiteCertificationPw, modifiedTime,userId);
        }

        */

    @Override
    public Long siteManagerSave(SiteManagerSaveDto dto) {

        //이메일은 고유값임. 중복체크
        //이거 이메일 null로 들어가도되서 중복으로 체크되는 것 같아서 추가
        if (StringUtils.hasText(dto.getUserEmail()) && userInfoRepository.existsByUserEmailAndHasDeleted(dto.getUserEmail(), Boolean.FALSE)) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        String certificationPw = null;

        //하차지 인증p/w
        if (StringUtils.hasText(dto.getSiteCertificationPw())) {
            certificationPw = checkUtil.numberCheck(dto.getSiteCertificationPw());

            if (certificationPw.length() < 4) {
                throw new BadRequestException("비밀번호는 숫자 4자 이상!");
            } else if (StringUtils.containsWhitespace(certificationPw)) {
                throw new BadRequestException("공백 입력은 불가능합니다");
            }
        }

        String phoneNumber = dto.getUserPhone();
        if (StringUtils.hasText(dto.getUserPhone())) {
            if (userInfoRepository.existsByUserPhoneAndHasDeleted(phoneNumber, Boolean.FALSE)) {
                throw new BadRequestException("이미 사용 중인 핸드폰 번호입니다.");
            }
            phoneNumber = checkUtil.phoneNumCheck(dto.getUserPhone());
        }

        String email = dto.getUserEmail();
        if (StringUtils.hasText(dto.getUserEmail())) {
            email = checkUtil.emailCheck(dto.getUserEmail());
        }

        String typeCode;
        if (dto.getSiteTypeCode().equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
            typeCode = CommonCode.SITE_TYPE_START_SITE.getCode();
        } else if (dto.getSiteTypeCode().equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {
            typeCode = CommonCode.SITE_TYPE_END_SITE.getCode();
        } else {
            throw new BadRequestException("현장 구분 입력이 안되었습니다.");
        }

        String bcryptPwd;

        if (StringUtils.hasText(dto.getUserEmail())) {
            bcryptPwd = passwordEncoder.encode(email);
        } else {
            // 랜덤한 4자리 숫자 스트링 생성
            Random random = new Random();
            int randomNumber = random.nextInt(9000) + 1000;  // 1000 이상 9999 이하의 난수 생성
            String randomPassword = String.valueOf(randomNumber);
            bcryptPwd = passwordEncoder.encode(randomPassword);
        }

        UserInfoEntity userInfoEntity = userInfoRepository.save(
                UserInfoEntity.builder()
                        .userName(dto.getUserName())
                        .userPhone(phoneNumber)
                        .userEmail(email)
                        .userPw(bcryptPwd)
                        .userAuthCode(CommonCode.AUTH_SITE.getCode())
                        .userTypeCode(CommonCode.AUTH_SITE.getCode())
                        .build());

        SiteManagerEntity siteManagerEntity = siteMemberRepository.save(SiteManagerEntity.builder()
                .userId(userInfoEntity)
                .siteTypeCode(typeCode)
                .weightBridgeType(dto.isWeighBridgeType())
                .activation(false)
//                .autoLogin(false)
                .siteName(dto.getSiteName())
                .address(dto.getAddress())
                .zipCode(dto.getZipCode())
                 //.addressDetail(dto.getAddressDetail())
                .roadAddress(dto.getRoadAddress())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
//                .firstLogin(true)
                .build());

        SiteManagerCertificationEntity siteManagerCertificationEntity = siteManagerCertificationRepository.save(
                SiteManagerCertificationEntity.builder()
                        .siteId(siteManagerEntity)
                        .siteCertificationPw(certificationPw)
                        .build());

        return userInfoEntity.getId();
    }

    @Override
    public Page<SiteManagerList> siteManagerSearchList(Pageable pageable, String searchType, String keyword, String siteTypeCode) {

        if(siteTypeCode == null) {
            siteTypeCode = "";
        }

        if (searchType == null) {
            return siteMemberRepository.siteManagerList(pageable, siteTypeCode);
        }
        switch (searchType) {
            case "회원명":
                return siteMemberRepository.searchOfUserNameManagerList(pageable, keyword, siteTypeCode);
            case "연락처":
                return siteMemberRepository.searchOfUserPhoneManagerList(pageable, keyword, siteTypeCode);
            case "현장명":
                return siteMemberRepository.searchOfSiteNameManagerList(pageable, keyword, siteTypeCode);
            default:
                return siteMemberRepository.siteManagerList(pageable, siteTypeCode);
        }
    }

    @Override
    public List<SiteAreaMapInfo> defaultSiteMapSearch(double longitude1, double latitude2,
                                                      double longitude2, double latitude1,String searchType) {


        if (longitude1 < -180 || longitude1 > 180 ||
                longitude2 < -180 || longitude2 > 180 ||
                latitude1 < -90 || latitude1 > 90 ||
                latitude2 < -90 || latitude2 > 90 ||
                longitude1 > longitude2 || latitude1 < latitude2) {
            throw new BadRequestException("잘못된 위도 경도값이 입력되었습니다.");
        }

        //상차지
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteMemberRepository.defaultSiteMapSearch(longitude1, latitude2, longitude2, latitude1, searchType);
        }
        //하차지
        else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {

            return siteMemberRepository.defaultSiteMapSearch(longitude1, latitude2, longitude2, latitude1, searchType);

        }
        else {

            throw new BadRequestException("현장 구분 입력이 안되었습니다.");
        }
    }


    @Override
    public SiteManagerInfo siteManagerInfo(Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다."));
        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));

        return siteMemberRepository.siteManagerInfo(userId);

    }

    @Override
    public ResponseEntity<byte[]> qrCodeExport(Long siteId) throws IOException, WriterException {

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findById(siteId).orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));

        Long siteMemberId = siteManagerEntity.getId();

        //String certificationPw = siteManagerCertificationRepository.qrPwd(siteId);//컨셉 변경으로 미사용.

        LocalDateTime modifyTime = siteManagerCertificationRepository.qrCreateTime(siteId);//컨셉 변경. 인증 pw는 qr과 별개.

        //String modifyTimeChar = modifyTime.toString();

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        //String formattedDateTime = modifyTime.format(formatter);

        String qrData = siteMemberId.toString()+modifyTime.toString();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String codeUrl = new String(qrData.toString().getBytes("UTF-8"), "ISO-8859-1");

        //QR Code의 Width, Height 값
        BitMatrix bitMatrix = qrCodeWriter.encode(codeUrl, BarcodeFormat.QR_CODE, 2000, 2000);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        }

    }

    @Override
    public void siteManagerQRCodeDownload(HttpServletResponse response, Long siteId) throws IOException, WriterException {

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findById(siteId).orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));

        Long siteMemberId = siteManagerEntity.getId();

        // QR 코드 데이터 생성
        //String certificationPw = siteManagerCertificationRepository.qrPwd(siteId);

        LocalDateTime modifyTime = siteManagerCertificationRepository.qrCreateTime(siteId);

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        //String formattedDateTime = modifyTime.format(formatter);

        String qrData = siteMemberId.toString()+modifyTime.toString();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String codeUrl = new String(qrData.toString().getBytes("UTF-8"), "ISO-8859-1");
        BitMatrix bitMatrix = qrCodeWriter.encode(codeUrl, BarcodeFormat.QR_CODE, 2000, 2000);

        // 이미지 생성
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // HttpServletResponse를 이용하여 파일 다운로드
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment; filename=QRCode.png");
        response.setHeader("Content-Transfer-Encoding", "binary");

        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(qrCodeImage, "png", outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void siteManagerNewQrCode(Long siteId, String certificationPw) {

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findById(siteId).orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));

        Optional<SiteManagerCertificationEntity> siteManagerCertificationEntity = siteManagerCertificationRepository.findBySiteIdAndHasDeleted(siteManagerEntity, Boolean.FALSE);

        LocalDateTime currentTime = LocalDateTime.now();

        siteManagerCertificationRepository.siteManagerCertificationUpdate(certificationPw, currentTime, siteManagerEntity.getUserId().getId());
    }
}