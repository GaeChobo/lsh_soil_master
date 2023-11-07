package kr.movements.smv2.entity;

import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "contract")
public class ContractEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "start_site_id", foreignKey = @ForeignKey(name = "fk_site_contract_st"), nullable = false)
    private SiteManagerEntity startSiteId;

    @OneToOne
    @JoinColumn(name = "end_site_id", foreignKey = @ForeignKey(name = "fk_site_contract_end"), nullable = false)
    private SiteManagerEntity endSiteId;

}
