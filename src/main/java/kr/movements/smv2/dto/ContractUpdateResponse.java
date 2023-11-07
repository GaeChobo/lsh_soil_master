package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("계약서 수정 response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractUpdateResponse {
    @ApiModelProperty(value = "자재 추가")
    private List<MaterialDto> materialAdd;
    @ApiModelProperty(value = "자재 수정")
    private List<MaterialUpdateDto> materialUpdate;
    @ApiModelProperty(value = "자재 삭제")
    private List<MaterialDeleteDto> materialDelete;
}
