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
@Table(name = "reason")
public class ReasonEntity extends BaseEntity {

    @Column(nullable = false)
    private String reason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="waybill_id", foreignKey = @ForeignKey(name = "fk_reason_waybill"), nullable = false)
    private WaybillEntity waybill;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="file_id", foreignKey = @ForeignKey(name = "fk_reason_file"))
    private FileEntity file;
}
