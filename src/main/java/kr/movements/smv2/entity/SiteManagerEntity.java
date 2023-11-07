package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SITE_MANAGER")
public class SiteManagerEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private UserInfoEntity userId;

    @Column(name = "site_type_code" , nullable = false, length = 4)
    private String  siteTypeCode;

    @Column(nullable = false)
    private boolean weightBridgeType;

    @Column(name = "activation" , nullable = false)
    private boolean activation;

    @Column(name = "site_name" , nullable = false, length = 50)
    private String siteName;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String roadAddress;

    @Column(length = 6)
    private String zipCode;

    @Column(length = 50)
    private String addressDetail;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

}

