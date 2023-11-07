package kr.movements.smv2.repository;

import kr.movements.smv2.entity.DriverOauthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverOauthRepository extends JpaRepository<DriverOauthEntity, Long> {

    Optional<DriverOauthEntity> findByProviderId(Long id);
    Optional<DriverOauthEntity> findByUserInfoId(Long userInfoId);

    //kakao 로그인 관련 추가
//    Optional<DriverOauthEntity> findByProviderIdAndProviderAndUsername(String providerId, String provider, String username);
    Optional<DriverOauthEntity> findByProviderIdAndProviderAndEmail(String providerId, String provider, String email);
    DriverOauthEntity findByUsername(String userName);
    DriverOauthEntity findByEmail(String email);
    Optional<DriverOauthEntity> findByProviderIdAndProviderAndEmailAndHasDeleted(String providerId, String provider, String email, boolean hasDeleted);
}
