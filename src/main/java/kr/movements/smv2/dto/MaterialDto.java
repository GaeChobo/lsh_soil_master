package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@ApiModel("계약서 자재 추가 Dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDto {
    @ApiModelProperty(value = "자재코드")
    private String materialCode;
    @Size(min = 1, max = 999999999)
    @ApiModelProperty(value = "계약물량")
    private int contractVolume;

}
