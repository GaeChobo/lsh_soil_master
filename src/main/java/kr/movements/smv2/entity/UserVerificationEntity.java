package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_VERIFICATION")
public class UserVerificationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfoEntity userId;

    @Column(name = "verification_code" , nullable = false, length = 6)
    private String verificationCode;

    @Column(name = "verification_status" , nullable = false)
    private boolean verificationStatus;

    @Column(nullable = false, length = 50)
    private String email;

}
