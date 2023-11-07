package kr.movements.smv2.service;

import kr.movements.smv2.dto.EmailMatchDto;
import kr.movements.smv2.dto.EmailSendDto;

public interface EmailService{

    public void matchEmailCode(EmailMatchDto dto);

    public void initPwSendEmail(EmailSendDto dto);

    public String sendMail(String Text) throws Exception;
}
