package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.exception.BootException;
import kr.movements.smv2.common.exception.NotFoundException;
import kr.movements.smv2.common.util.RegexUtil;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.*;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service("driverMemberService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverMemberServiceImpl implements DriverMemberService{

    private final DriverMemberRepository driverMemberRepository;
    private final UserInfoRepository userInfoRepository;
    private final DriverOauthRepository driverOauthRepository;
    private final RegexUtil regexUtil;
    private final WaybillRepository waybillRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SmsVerificationRepository smsVerificationRepository;

    @Value("${naver.sms.access_key}")
    private String NCP_ACCESS_KEY;
    @Value("${naver.sms.secret_key}")
    private String NCP_SECRET_KEY;
    @Value("${naver.sms.service_id}")
    private String NCP_SERVICE_ID;
    @Value("${naver.sms.url.host_url}")
    private String NCP_SMS_HOST_URL;
    @Value("${naver.sms.url.request_url}")
    private String NCP_SMS_REQUEST_URL;
    @Value("${naver.sms.phone.number}")
    private String NCP_SMS_PHONE_NUMBER;
/*
    @Override
    @Transactional
    public void driverSave(DriverSaveDto dto) {

        String carNumber = dto.getCarNumber();
        if(StringUtils.hasText(dto.getCarNumber())) { //차량번호가 있다면 검증
            carNumber = regexUtil.carNumCheck(dto.getCarNumber());
        }

        UserInfoEntity userInfoEntity = userInfoRepository.save(UserInfoEntity.builder()
                .userName(dto.getUserName())
                .userPhone(dto.getUserPhone())
                .userEmail(dto.getUserEmail())
                .userPw("비밀번호")
                .userToken(dto.getUserToken())
                .userAuthCode("1030")
                .userTypeCode("1030")
                .build());

        driverMemberRepository.save(DriverEntity.builder()
                .carNumber(carNumber)
                .driverCompany(dto.getDriverCompanyName())
                .personalInfoAgree(dto.isPersonalInfoAgree()) //앱 초기 실행 시 동의상태.
                .personalInfoAgreeDate(LocalDate.now())
                .termsOfServiceAgree(dto.isTermsOfServiceAgree()) //앱 초기 실행 시 동의상태.
                .termsOfServiceAgreeDate(LocalDate.now())
                .userInfoId(userInfoEntity)
                .gpsAgree(dto.isGpsAgree()) //앱 초기 실행 시 동의상태.
                .gpsAgreeDate(LocalDate.now())
                .build());
        driverOauthRepository.save(DriverOauthEntity.builder()
                .snsTypeCode("kakao")
                .providerKey("randomkakaoKey")
                .oauthName(dto.getOauthUserName())
                .oauthEmail(dto.getOauthUserEmail())
//                .oauthPhone(dto.getOauthUserPhone())
                .userInfoId(userInfoEntity)
                .build());

        return DriverSaveDto.builder()
                .userId(userInfoEntity.getId())
                .userName(userInfoEntity.getUserName())
                .userPhone(userInfoEntity.getUserPhone())
                .userEmail(userInfoEntity.getUserEmail())
                .userAuth(userInfoEntity.getUserAuth())
                .loginType(userInfoEntity.getLoginType())
                .driverCarNumber(driverEntity.getDriverCarNumber())
                .driverCompanyName(driverEntity.getDriverCompany())
                .personalInfoAgree(driverEntity.isPersonalInfoAgree())
                .termsOfServiceAgree(driverEntity.isTermsOfServiceAgree())
                .build();

    }

 */


    @Override
    public Page<DriverListDto> driverList(String searchType, String keyword, Pageable pageable) {

        return driverMemberRepository.findAllDriverList(searchType, keyword, pageable);
    }

    @Override
    public DriverInfoDto driverInfo(Long userId) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다."));;
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사정보를 확인 할 수 없습니다."));
        Long Count = waybillRepository.findByDriverTotalPassCount(driverEntity.getId());


        return DriverInfoDto.builder()
                .userId(userInfoEntity.getId())
                .driverId(driverEntity.getId())
                .userName(userInfoEntity.getUserName())
                .userPhone(userInfoEntity.getUserPhone())
                .userEmail(userInfoEntity.getUserEmail())
                .carNumber(driverEntity.getCarNumber())
                .driverCompany(driverEntity.getDriverCompany())
                .totalWaybill(Count)
                .build();
    }

    @Override
    @Transactional
    public void driverUpdate(Long userId, DriverUpdateDto dto) {
        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다."));
        DriverEntity driverEntity = driverMemberRepository.findByUserInfoIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("운송기사정보를 확인 할 수 없습니다."));

        String carNum = "";
        String companyName = "";

        //차량번호 정규식 검사
        if(StringUtils.hasText(dto.getDriverCarNumber())) {
            carNum = regexUtil.carNumCheck(dto.getDriverCarNumber());
        }

        //회사명 있으면 업데이트
        if (StringUtils.hasText(dto.getDriverCompanyName())) {
            companyName = dto.getDriverCompanyName();
        }
        userInfoEntity.aUpdate(
                dto.getUserName(),
                dto.getUserPhone());
        driverEntity.aUpdate(
                carNum,
                companyName);
    }

    @Override
    @Transactional
    public void driverAppDelete(Long userId, String userName) {
        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다."));
        DriverOauthEntity driverOauthEntity = driverOauthRepository.findByUserInfoId(userInfoEntity.getId()).orElseThrow(() -> new BadRequestException("oauth 인증정보를 확인 할 수 없습니다."));

        if(!userInfoEntity.getUserName().equals(userName)) {
            throw new BadRequestException("사용자 이름이 일치하지않습니다.");
        }

        userInfoEntity.setHasDeleted(true);
        driverOauthEntity.setHasDeleted(true);
    }

    @Override
    @Transactional
    public void driverDelete(Long userId) {
        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("회원정보를 확인 할 수 없습니다."));
        DriverOauthEntity driverOauthEntity = driverOauthRepository.findByUserInfoId(userInfoEntity.getId()).orElseThrow(() -> new BadRequestException("oauth 인증정보를 확인 할 수 없습니다."));

        userInfoEntity.setHasDeleted(true);
        driverOauthEntity.setHasDeleted(true);
    }

    @Override
    public Page<WaybillDriverListDto> waybillDriverList(String searchType, String keyword, Pageable pageable) {
        return driverMemberRepository.findAllSearchDriverList(searchType, keyword, pageable);
    }

    @Override
    public Page<TransportNotPassListResponse> transportFailureList(String searchType, String keyword, Pageable pageable) {
        return driverMemberRepository.findAllTransportFailureList(searchType, keyword, pageable);
    }

    @Override
    @Transactional
    public void driverMemberSave(DriverMemberSaveDto dto) {
        String carNumber = dto.getCarNumber();
        String companyName = "";
        if(StringUtils.hasText(dto.getCarNumber())) { //차량번호가 있다면 검증
            carNumber = regexUtil.carNumCheck(dto.getCarNumber());
        }
        if(StringUtils.hasText(dto.getDriverCompanyName())) { //회사명이 있다면 검증
            companyName = regexUtil.companyNameCheck(dto.getDriverCompanyName());
        }
        //사용중인 이메일인지 검사
        if(userInfoRepository.existsByUserEmailAndHasDeleted(dto.getEmail(), false)) throw new BadRequestException("이미 사용중인 이메일 입니다.");

        //kakao_14223231_akj1432@gmail.com
        String password = dto.getProvider() + "_" + dto.getProviderId() + "_" + dto.getEmail();

        Optional<DriverOauthEntity> driverOauthEntity = driverOauthRepository.findByProviderIdAndProviderAndEmailAndHasDeleted(dto.getProviderId(), dto.getProvider(), dto.getEmail(), false);

        //oauth 인증 정보가 db에 없으면 insert
        DriverOauthEntity driverOauth;
        if(!driverOauthEntity.isPresent()) {
             driverOauth = driverOauthRepository.save(DriverOauthEntity.builder()
                    .provider(dto.getProvider())
                    .providerId(dto.getProviderId())
                    .username(dto.getProvider() +"_"+ dto.getProviderId())
                    .password(passwordEncoder.encode(password))
                    .role(CommonCode.AUTH_DRIVER.getCode())
                    .email(dto.getEmail())
                    .build());
        }else {
            driverOauth = driverOauthEntity.get();
        }

        //kakao_14223231_akj1432@gmail.com
//        String password = passwordEncoder.encode(dto.getProvider() + "_" + dto.getProviderId() + "_" + dto.getEmail());
        UserInfoEntity userInfoEntity = userInfoRepository.save(UserInfoEntity.builder()
                .userName(dto.getUserName())
                .userPhone(dto.getUserPhone())
                .userEmail(dto.getEmail())
                .userPw(passwordEncoder.encode(password))
                .userAuthCode(CommonCode.AUTH_DRIVER.getCode())
                .userTypeCode(CommonCode.AUTH_DRIVER.getCode())
                .build());

        driverMemberRepository.save(DriverEntity.builder()
                .carNumber(carNumber)
                .driverCompany(dto.getDriverCompanyName())
                .personalInfoAgree(dto.isPersonalInfoAgree())
                .personalInfoAgreeDate(LocalDate.now())
                .termsOfServiceAgree(dto.isTermsOfServiceAgree())
                .termsOfServiceAgreeDate(LocalDate.now())
                .userInfoId(userInfoEntity)
//                .gpsAgreeDate(LocalDate.now())
                .driverCompany(companyName)
                .build());

        driverOauth.setUserInfoId(userInfoEntity.getId());
//        driverOauthRepository.save(DriverOauthEntity.builder()
//                .provider("kakao")
//                .providerId(dto.getProviderUserNumber())
////                .providerKey("randomkakaoKey")
////                .oauthName(dto.getOauthUserName())
//                .email(dto.getOauthUserEmail())
////                .oauthPhone(dto.getOauthUserPhone())
//                .userInfoId(userInfoEntity.getId())
//                .build());

    }


    @Override
    public void sendSms(SmsDto smsDto) {
        //이미 사용중인 휴대폰 번호인지 확인,
        //TODO: userType까지 확인할지 고민.(1030)
        String phone = smsDto.getTel().substring(0,3)+"-"+smsDto.getTel().substring(3,7)+"-"+smsDto.getTel().substring(7,11);
        if(userInfoRepository.existsByUserPhoneAndHasDeleted(phone, false)) {
            throw new BadRequestException("이미 사용중인 연락처입니다");
        }

        //문자 발송, 1분 이내에는 재발송 불가
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMinutes(1);
        if(smsVerificationRepository.existsByPhoneAndCreatedDateBetween(phone, startDate, endDate)) {
            throw new BadRequestException("인증번호 재발송은 1분이 지나야 가능합니다");
        }

        String hostNameUrl = NCP_SMS_HOST_URL;            // 호스트 URL
        String requestUrl = NCP_SMS_REQUEST_URL;                        // 요청 URL
        String requestUrlType = "/messages";                            // 요청 URL
        String accessKey = NCP_ACCESS_KEY;                        // 네이버 클라우드 플랫폼 회원에게 발급되는 개인 인증키			// Access Key : https://www.ncloud.com/mypage/manage/info > 인증키 관리 > Access Key ID
        String secretKey = NCP_SECRET_KEY;  // 2차 인증을 위해 서비스마다 할당되는 service secret key	// Service Key : https://www.ncloud.com/mypage/manage/info > 인증키 관리 > Access Key ID
        String serviceId = NCP_SERVICE_ID;       // 프로젝트에 할당된 SMS 서비스 ID							// service ID : https://console.ncloud.com/sens/project > Simple & ... > Project > 서비스 ID
        String method = "POST";                                            // 요청 method
        String timestamp = Long.toString(System.currentTimeMillis());    // current timestamp (epoch)
        requestUrl += serviceId + requestUrlType;
        String apiUrl = hostNameUrl + requestUrl;
        String smsKey = createSmsKey();

        // JSON 을 활용한 body data 생성
        JSONObject bodyJson = new JSONObject();
        JSONObject toJson = new JSONObject();
        JSONArray toArr = new JSONArray();

        toJson.put("to", smsDto.getTel());                        // Mandatory(필수), messages.to	수신번호, -를 제외한 숫자만 입력 가능
        toArr.put(toJson);

        bodyJson.put("type", "SMS");                            // Madantory, 메시지 Type (SMS | LMS | MMS), (소문자 가능)
        //bodyJson.put("contentType","");					// Optional, 메시지 내용 Type (AD | COMM) * AD: 광고용, COMM: 일반용 (default: COMM) * 광고용 메시지 발송 시 불법 스팸 방지를 위한 정보통신망법 (제 50조)가 적용됩니다.
        //bodyJson.put("countryCode","82");					// Optional, 국가 전화번호, (default: 82)
        bodyJson.put("from", NCP_SMS_PHONE_NUMBER);                    // Mandatory, 발신번호, 사전 등록된 발신번호만 사용 가능
        //bodyJson.put("subject","");						// Optional, 기본 메시지 제목, LMS, MMS에서만 사용 가능
        bodyJson.put("content", "[SOIL Master] 인증번호 ["+smsKey+"]를 입력해주세요.");    // Mandatory(필수), 기본 메시지 내용, SMS: 최대 80byte, LMS, MMS: 최대 2000byte
        bodyJson.put("messages", toArr);                    // Mandatory(필수), 아래 항목들 참조 (messages.XXX), 최대 1,000개

        String body = bodyJson.toString();

        try {
            URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            con.setRequestProperty("x-ncp-iam-access-key", accessKey);
            con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
            con.setRequestMethod(method);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            wr.write(body.getBytes());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 202) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

        } catch (Exception e) {
            throw new BootException(e);
        }

        //db에 인증정보 저장
        smsVerificationRepository.save(SmsVerificationEntity.builder()
                        .verificationCode(smsKey)
                        .verificationStatus(false)
                        .phone(smsDto.getTel())
                .build());
    }

    @Override
    public void receiveSms(SmsReceiveDto smsReceiveDto) {
        //현재 시간에서 5분전까지 조회
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endDate = LocalDateTime.now();

        SmsVerificationEntity smsVerificationEntity = smsVerificationRepository.findTopByPhoneAndVerificationStatusAndCreatedDateBetweenOrderByCreatedDateDesc(smsReceiveDto.getTel(), false, startDate, endDate).orElseThrow(() -> new BadRequestException("인증번호를 다시 발송해 주세요"));

        if(!smsReceiveDto.getSmsKey().equals(smsVerificationEntity.getVerificationCode())) {
            throw new BadRequestException("인증번호가 일치하지 않습니다");
        }

        //인증 성공으로 변경
        smsVerificationEntity.successVerification(true);
    }

    private String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                 // new line

        String message = new StringBuilder()
            .append(method)
            .append(space)
            .append(url)
            .append(newLine)
            .append(timestamp)
            .append(newLine)
            .append(accessKey)
            .toString();

        SecretKeySpec signingKey;
        String encodeBase64String;

        signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }


        private String createSmsKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 5; i++) { // 인증코드 5자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }
}