package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.util.CheckUtil;
import kr.movements.smv2.common.util.EmailUtil;
import kr.movements.smv2.dto.EmailMatchDto;
import kr.movements.smv2.dto.EmailSendDto;
import kr.movements.smv2.dto.EmailSenderDto;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.UserVerificationEntity;
import kr.movements.smv2.repository.SiteManagerRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import kr.movements.smv2.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailUtil emailUtil;

    private final UserInfoRepository userInfoRepository;

    private final UserVerificationRepository userVerificationRepository;

    private final SiteManagerRepository siteManagerRepository;

    private final CheckUtil checkUtil;

//   @Autowired
    private final JavaMailSender javaMailSender;

    @Autowired
    private Environment environment;


    @Override
    @Transactional
    public void matchEmailCode(EmailMatchDto dto) {
/*
        UserInfoEntity userInfoEntity = userInfoRepository.findById(dto.getUserId()).orElseThrow(() ->
                new BadRequestException("유저정보를 확인 할 수 없습니다."));

        //인증번호 created_date
        //어제만든 코드면 : created_date 어제임.
        LocalDateTime verTime = userVerificationRepository.userVerficationTime(dto.getUserId(), dto.getVerificationCode());

        LocalDateTime expiredTime = verTime.plusSeconds(300);//생성시간 + 300초

        LocalDateTime now = LocalDateTime.now();

        if(expiredTime.isBefore(now)) {

            throw new BadRequestException("인증시간 5분 지남");

        } else {

            userVerificationRepository.userVerficationUpdate(dto.getUserId(), dto.getVerificationCode());

            siteManagerRepository.siteManagerActivationUpdate(dto.getUserId());
        }

 */
        //인증테이블의 가장 최근 레코드 조회.
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(dto.getUserId(), false).orElseThrow(() -> new BadRequestException("사용자정보를 확인할 수 없습니다"));
        UserVerificationEntity userVerificationEntity = userVerificationRepository.findTopByUserIdOrderByCreatedDateDesc(userInfoEntity).orElseThrow(() -> new BadRequestException("인증코드를 확인할 수 없습니다"));

        LocalDateTime expiredTime = userVerificationEntity.getCreatedDate().plusSeconds(300);//생성시간 + 300초

        if(expiredTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("인증시간 5분 지남");
        }else {
            if(dto.getVerificationCode().equals(userVerificationEntity.getVerificationCode())) { //인증코드

                userVerificationRepository.userVerficationUpdate(dto.getUserId(), dto.getVerificationCode());

                siteManagerRepository.siteManagerActivationUpdate(dto.getUserId());
            }else {
                throw new BadRequestException("인증코드가 일치하지 않습니다");
            }
        }

    }

    public String readHtmlFile(String filePath) throws IOException {
        byte[] htmlBytes = Files.readAllBytes(Paths.get(filePath));
        return new String(htmlBytes, "UTF-8");
    }

    @Override
    @Transactional
    public void initPwSendEmail(EmailSendDto dto) {

        String email = dto.getEmail();

        String senderEmail = environment.getProperty("email.sender");

        UserInfoEntity userInfoEntityOptional = userInfoRepository.findByIdAndHasDeleted(dto.getUserId(), Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("현장관리자정보를 확인 할 수 없습니다."));

        //사용중인 이메일인가?
        Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findByUserEmailAndHasDeleted(dto.getEmail(), false);
        if (userInfoEntity.isPresent()) {
            //다른사람이 사용중인 이메일이면 사용 불가.
            if(!userInfoEntity.get().getId().equals(dto.getUserId())) {
                throw new BadRequestException("이미 사용중인 이메일 입니다.");
            }
        }


        //인증메일 발송 1분 제한
        if(userVerificationRepository.existsTopByEmailAndCreatedDateBetweenOrderByCreatedDateDesc(dto.getEmail(), LocalDateTime.now().minusMinutes(1) ,LocalDateTime.now())) {
            throw new BadRequestException("인증번호 재발송은 1분이 지나야 가능합니다");
        }

        //메일 발송
        try {

            String verificationCode = emailUtil.generateRandomNumber(6);

            String htmlFilePath = "/home/ubuntu/emailImg/email-access.html";  // HTML 파일의 경로

            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), "UTF-8");

            String modifiedContent = htmlContent.replace("DH1234", verificationCode); //html의 소스를 변경하여 인증번호 삽입

            emailUtil.send(EmailSenderDto.builder()
                    .from(senderEmail)
                    .to(email)
                    .subject("SoilMaster 인증번호 안내")
                    .content(modifiedContent)
                    .build());

            userVerificationRepository.save(
            UserVerificationEntity.builder()
                    .userId(userInfoEntityOptional)
                    .verificationCode(verificationCode)
                    .verificationStatus(false)
                    .email(dto.getEmail())
                    .build());

        } catch(Exception e) {
            log.error("발송실패 대상자: " + email);
            throw new BadRequestException(this.getClass(), "발송 실패");
        }

    }

    @Override
    public String sendMail(String Text) throws Exception {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo("dltmdgml200@gmail.com");
        simpleMailMessage.setFrom("lsh.mv@movements.kr");
        simpleMailMessage.setSubject("Soil Master 인증번호 메일 전송");
        simpleMailMessage.setText("인증번호" + Text);

        try{
            javaMailSender.send(simpleMailMessage);

            return "success";

        }catch (Exception e) {

            return "fail";
        }





    }


}
