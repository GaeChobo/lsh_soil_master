package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_INFO")
//        indexes = { @Index(name = "idx_user_email", columnList = "userEmail", unique = true) })
public class UserInfoEntity extends BaseEntity {

    @Column(length = 45)
    private String userName;

    @Column(length = 13)
    private String userPhone;

    @Column(length = 45)
    private String userEmail;

    @Column
    private String userPw;

    @Column
    private String userToken;

    @Column(nullable = false, length = 45)
    private String userAuthCode; //권한

    @Column(nullable = false, length = 45)
    private String userTypeCode; //admin, site, driver

    public void aUpdate(String userName, String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public void updateToken(String refreshToken){
        this.userToken = refreshToken;
    }

}