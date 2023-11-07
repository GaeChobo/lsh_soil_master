package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SMS_VERIFICATION")
public class SmsVerificationEntity extends BaseEntity {

    @Column(nullable = false, length = 6)
    private String verificationCode;

    @Column(nullable = false)
    private boolean verificationStatus;

    @Column(nullable = false, length = 11)
    private String phone;

    public void successVerification(boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
