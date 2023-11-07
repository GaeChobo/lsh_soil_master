package kr.movements.smv2.repository;

import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.UserVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerificationEntity, Long> {

    @Query(value = "select count(*) from user_info\n" +
            "where id = :userId and user_email = :email", nativeQuery = true)
    int userEmailAndIdCheck(@Param("userId") Long userID, @Param("email") String email);

    @Query(value = "select \n" +
            "\tcreated_date\n" +
            "from user_verification\n" +
            "where user_id = :userId and verification_code = :verificationCode", nativeQuery = true)
    LocalDateTime userVerficationTime(@Param("userId") Long userID, @Param("verificationCode") String verificationCode);

    @Modifying
    @Query(value = "update user_verification set\n" +
            "\tverification_status = true\n" +
            "where user_id = :userId and verification_code = :verificationCode", nativeQuery = true)
    void userVerficationUpdate(@Param("userId") Long userID, @Param("verificationCode") String verificationCode);

    boolean existsByUserIdAndEmailAndAndVerificationStatus(UserInfoEntity userId, String email, boolean verificationStatus);

    Optional<UserVerificationEntity> findTopByUserIdOrderByCreatedDateDesc(UserInfoEntity userId);

    boolean existsTopByEmailAndCreatedDateBetweenOrderByCreatedDateDesc(String  email, LocalDateTime startDate, LocalDateTime endDate);

}
