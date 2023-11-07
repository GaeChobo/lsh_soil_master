package kr.movements.smv2.repository;

import kr.movements.smv2.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {

    Long countByUserEmailAndHasDeleted(String userEmail, boolean hasDeleted);

    @Modifying
    @Query(value = "update user_info set\n" +
            "\tuser_pw = :userPw\n" +
            "where id = :userId and has_deleted = false", nativeQuery = true)
    void userPwdUpdate(@Param("userPw") String userPw, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update user_info set\n" +
            "\tuser_name = :userName,\n" +
            "\tuser_phone = :userPhone,\n" +
            "\tuser_email = :userEmail\n" +
            "where id = :userId", nativeQuery = true)
    void userInfoUpdate(@Param("userName") String userName, @Param("userPhone") String userPhone, @Param("userEmail") String userEmail, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update user_info set\n" +
            "\tuser_name = :userName,\n" +
            "\tuser_phone = :userPhone,\n" +
            "\tuser_email = :userEmail,\n" +
            "\tuser_pw = :userPw\n" +
            "where id = :userId", nativeQuery = true)
    void SiteUserInfoUpdate(@Param("userName") String userName, @Param("userPhone") String userPhone, @Param("userEmail") String userEmail, @Param("userPw") String userPw ,@Param("userId") Long userId);

    Optional<UserInfoEntity> findByIdAndHasDeleted(Long id, Boolean hasDeleted);
    Optional<UserInfoEntity> findByIdAndUserAuthCodeAndHasDeleted(Long id, String userAuthCode, Boolean hasDeleted);
    Optional<UserInfoEntity> findByUserEmailAndHasDeleted(String userEmail, Boolean hasDeleted);
    boolean existsByUserEmailAndHasDeleted(String userEmail, boolean hasDeleted);
    boolean existsByUserPhoneAndHasDeleted(String userPhone, boolean hasDeleted);

}
