package kr.movements.smv2.service;

import kr.movements.smv2.dto.InquiryDetailDto;
import kr.movements.smv2.dto.InquiryDto;
import kr.movements.smv2.entity.InquiryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface InquiryService {

    void inquiryRegister(Long userId, String inquiryCode, String email,String inquiryText, Long fileId);

    Page<InquiryDto> inquiryList(Long userId, Pageable pageable);

    InquiryDetailDto inquiryDetail(Long inquiryId) throws IOException;

    Page<InquiryDto> adminInquiryList(Pageable pageable);
}
