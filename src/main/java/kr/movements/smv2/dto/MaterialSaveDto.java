package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("자재 추가 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialSaveDto {

    @ApiModelProperty(value = "계약서 ID")
    private Long contractId;
    @ApiModelProperty(value = "자재 ID, 자재 수정 시에만 사용", example = "자재 ID값 자재 수정시에만 사용, 비사용시 0으로")
    private Long materialId;
    @ApiModelProperty(value = "자재 코드")
    private String materialCode;
    @ApiModelProperty(value = "계약 수량")
    private int contractVolume;
    @ApiModelProperty(value = "누적 운송량")
    private int transVolume;
    @ApiModelProperty(value = "자재 생성,수정,제거 Type", example = "1 : 자재 생성 ,2 : 자재 수정, 3 : 자재 제거")
    private int materialInputType;
}
