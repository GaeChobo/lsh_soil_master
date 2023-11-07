package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRIVER_OAUTH")
public class DriverOauthEntity extends BaseEntity {
/*
    @Column
    private String snsTypeCode;
    @Column
    private Long providerUserNumber;
    @Column
    private String oauthEmail;
    @OneToOne
    @JoinColumn(name = "user_info_id", foreignKey = @ForeignKey(name = "fk_driver_oauth_user"), nullable = false)
    private UserInfoEntity userInfoId;

 */
    @Column(nullable = false, length = 40)
    private String username;
    @Column
    private String password;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false, length = 4)
    private String role;
    @Column(nullable = false, length = 20)
    private String provider;
    @Column(nullable = false, length = 20)
    private String providerId;
    @Column
    private Long userInfoId;

    @Builder
    public DriverOauthEntity(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public DriverOauthEntity(String username, String password, String email, String role, String provider, String providerId, Long userInfoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.userInfoId = userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }
/*
    public DriverOauthEntity update(OAuth2UserInfo oAuth2UserInfo) {
        this.username = oAuth2UserInfo.getName();
        this.providerId = oAuth2UserInfo.getProviderId();
        return this;
    }

 */
}
