package kr.movements.smv2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "waybill")
public class WaybillEntity extends BaseEntity {

    @Column(nullable = false, length = 16)
    private String waybillNum;
    @Column(nullable = false, length = 3)
    private Integer transportVolume;
    @Column(nullable = false, length = 10)
    private String  carNumber;
    @JsonFormat(timezone = "Asia/Seoul")
    @Column
    private LocalDateTime departureTime;
    @JsonFormat(timezone = "Asia/Seoul")
    @Column
    private LocalDateTime waitTime;
    @JsonFormat(timezone = "Asia/Seoul")
    @Column
    private LocalDateTime arriveTime;
    @Column(nullable = false, length = 4)
    private String waybillStatusCode;
    @Column
    private Integer ete; //예상소요시간
    @Column(nullable = false, length = 50)
    private String startSiteName;
    @Column(nullable = false, length = 50)
    private String endSiteName;
    @Column(nullable = false, length = 4)
    private String materialTypeCode;
    @Column(nullable = false)
    private boolean gpsAgree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contract_id", foreignKey = @ForeignKey(name = "fk_waybill_contract"), nullable = false)
    private ContractEntity contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="driver_id", foreignKey = @ForeignKey(name = "fk_waybill_driver"), nullable = false)
    private DriverEntity driver;
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public void gpsUpdate(boolean gpsAgree) {
        this.gpsAgree = gpsAgree;
    }
    public void updateUnprocessed(String waybillStatusCode) {
        this.waybillStatusCode = waybillStatusCode;
        arriveTime = LocalDateTime.now();
    }
}
