package kr.movements.smv2.repository;

import kr.movements.smv2.dto.InquiryDto;
import kr.movements.smv2.entity.InquiryEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long>, InquiryRepositoryCustom {

    Optional<InquiryEntity> findByIdAndHasDeleted(Long inquiryId, Boolean hasDeleted);

}
