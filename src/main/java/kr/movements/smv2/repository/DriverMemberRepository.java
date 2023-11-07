

package kr.movements.smv2.repository;

import kr.movements.smv2.entity.DriverEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Driver;
import java.util.List;
import java.util.Optional;

public interface DriverMemberRepository extends JpaRepository<DriverEntity, Long>, DriverMemberRepositoryCustom {
    Optional<DriverEntity> findByUserInfoIdAndHasDeleted(UserInfoEntity userInfoId, Boolean hasDeleted);


    @Query(value = "select\n" +
            "\tid as driverId\n" +
            "from driver\n" +
            "where has_deleted = false and user_info_id = :userInfoId", nativeQuery = true)
    Long findDriverId(@Param("userInfoId") Long userInfoId);
    Optional<DriverEntity> findByIdAndHasDeleted(Long id, boolean hasDeleted);

}


