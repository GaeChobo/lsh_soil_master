package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("계약서 자재 삭제 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDeleteDto {
    @ApiModelProperty(value = "자재 id")
    private Long materialId;
}
