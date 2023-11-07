package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("계약서 자재 dto")
public class MaterialListDto {

    @ApiModelProperty(value = "자재코드")
    private String materialCode;

    @ApiModelProperty(value = "계약물량")
    private Integer contractVolume;
}