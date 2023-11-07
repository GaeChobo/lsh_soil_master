package kr.movements.smv2.repository;

import kr.movements.smv2.entity.SmsVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsVerificationRepository extends JpaRepository<SmsVerificationEntity, Long> {
    Optional<SmsVerificationEntity> findTopByPhoneAndVerificationStatusAndCreatedDateBetweenOrderByCreatedDateDesc(String phone, boolean verificationStatus, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByPhoneAndCreatedDateBetween(String phone, LocalDateTime startDate, LocalDateTime endDate);
}
