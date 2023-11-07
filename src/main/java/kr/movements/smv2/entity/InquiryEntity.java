package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "INQUIRY")
public class InquiryEntity extends BaseEntity {

    @Column(name = "inquiry_code")
    private String inquiryCode;

    @Column(name = "inquiry_receive_email")
    private String receiveEmail;

    @Column(name = "inquiry_context")
    private String inquiryText;

    @JoinColumn(name = "user_info_id", foreignKey = @ForeignKey(name = "fk_inquiry_user"), nullable = false)
    @OneToOne
    private UserInfoEntity userInfoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="file_id", foreignKey = @ForeignKey(name = "fk_inquiry_file"))
    private FileEntity file;

}
