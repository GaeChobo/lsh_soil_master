package kr.movements.smv2.repository;

import kr.movements.smv2.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long>, AlarmRepositoryCustom {
//    Optional<AlarmEntity> findByMessage(String message);
}
