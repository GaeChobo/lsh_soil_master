package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class MaterialInfoList {

    private Long materialId;

    private String materialCode;

    private int contractVolume;

    private int transVolume;

    public MaterialInfoList(Long materialId, String materialCode, int contractVolume, int transVolume) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.contractVolume = contractVolume;
        this.transVolume = transVolume;
    }
}
