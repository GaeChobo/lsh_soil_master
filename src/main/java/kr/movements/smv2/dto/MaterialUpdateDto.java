package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@ApiModel("계약서 자재 수정 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialUpdateDto {
    @ApiModelProperty(value = "자재 id")
    private Long materialId;
    @ApiModelProperty(value = "자재 코드")
    private String materialCode;
    @Size(min = 1, max = 999999999)
    @ApiModelProperty(value = "계약 수량")
    private Integer contractVolume;
}
