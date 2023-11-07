package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SITE_MANAGER_CERTIFICATION")
public class SiteManagerCertificationEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "site_id" , nullable = false)
    private SiteManagerEntity siteId;

    @Column(name = "site_certification_pw")
    private String siteCertificationPw;

}
