package kr.movements.smv2.repository;

import kr.movements.smv2.entity.AgreeTermsConditionsEntity;
import kr.movements.smv2.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface AgreeTermsConditionsRepository extends JpaRepository<AgreeTermsConditionsEntity, Long> {


    List<AgreeTermsConditionsEntity> findByPolicyTypeAndHasDeletedOrderByCreatedDateDesc(String policyType, boolean hasDeleted);

    Optional<AgreeTermsConditionsEntity> findByIdAndHasDeleted(Long policyId, boolean hasDeleted);
}
