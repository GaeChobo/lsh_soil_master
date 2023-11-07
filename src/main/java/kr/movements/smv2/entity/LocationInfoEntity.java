package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "location_info")
public class LocationInfoEntity extends BaseEntity {
    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="waybill_id", foreignKey = @ForeignKey(name = "fk_location_info_waybill"), nullable = false)
    private WaybillEntity waybill;
}
