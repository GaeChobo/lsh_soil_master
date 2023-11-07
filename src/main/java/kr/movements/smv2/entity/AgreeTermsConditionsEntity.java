package kr.movements.smv2.entity;


import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "agree_terms_conditions")
public class AgreeTermsConditionsEntity extends BaseEntity {

    @Column(length = 4)
    private String policyType;

    @Column(columnDefinition = "Text")
    private String policyText;

    @Column
    private String policyTitle;
}
