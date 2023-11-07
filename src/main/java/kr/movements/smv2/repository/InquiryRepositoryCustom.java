package kr.movements.smv2.repository;

import kr.movements.smv2.dto.InquiryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {

    Page<InquiryDto> getInquiryList(Long userId, Pageable pageable);

    Page<InquiryDto> getAdminInquiryList(Pageable pageable);
}
