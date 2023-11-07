package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRIVER")
public class DriverEntity extends BaseEntity {

    @Column(name = "car_number" ,length = 10)
    private String carNumber;

    @Column(name = "driver_company" , length = 30)
    private String driverCompany;

    @Column(nullable = false)
    private boolean personalInfoAgree; //개인정보 동의여부
    @Column(nullable = false)
    private boolean termsOfServiceAgree; //서비스이용 동의여부
//    @Column(nullable = false)
//    private boolean gpsAgree; //gps정보 수집 동의여부
    @Column(nullable = false)
    private LocalDate personalInfoAgreeDate;
    @Column(nullable = false)
    private LocalDate termsOfServiceAgreeDate;
//    @Column(nullable = false)
//    private LocalDate gpsAgreeDate;

    @OneToOne
    @JoinColumn(name = "user_info_id", foreignKey = @ForeignKey(name = "fk_driver_user"), nullable = false)
    private UserInfoEntity userInfoId;

    public void aUpdate(String carNumber, String driverCompany) {
        this.carNumber = carNumber;
        this.driverCompany = driverCompany;
    }
//    public void gpsUpdate(boolean gpsAgree, LocalDate gpsAgreeDate) {
//        this.gpsAgree = gpsAgree;
//        this.gpsAgreeDate = gpsAgreeDate;
//    }
}