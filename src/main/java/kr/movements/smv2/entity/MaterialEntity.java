package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.format.SignStyle;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "material")
public class MaterialEntity extends BaseEntity {

    @Column(nullable = false, length = 4)
    private String materialCode;
    @Column(nullable = false, length = 9)
    private Integer contractVolume;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contract_id", foreignKey = @ForeignKey(name = "fk_material_contract"), nullable = false)
    private ContractEntity contract;

    public void update(Integer contractVolume) {
        this.contractVolume = contractVolume;
    }
}