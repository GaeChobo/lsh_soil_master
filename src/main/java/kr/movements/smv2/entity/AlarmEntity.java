package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "alarm")
public class AlarmEntity extends BaseEntity {
    @Column(nullable = false, length = 4)
    private String alarmTypeCode;

    @Column(nullable = false)
    private boolean alarmCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="waybill_id", foreignKey = @ForeignKey(name = "fk_alarm_waybill"), nullable = false)
    private WaybillEntity waybill;

    public void update(boolean alarmCheck) {
        this.alarmCheck = alarmCheck;
    }
}
