package kr.movements.smv2.repository;

import kr.movements.smv2.entity.ReasonEntity;
import kr.movements.smv2.entity.WaybillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReasonRepository extends JpaRepository<ReasonEntity, Long> {
    Optional<ReasonEntity> findByWaybillAndHasDeleted(WaybillEntity waybill, boolean hasDeleted);
}
