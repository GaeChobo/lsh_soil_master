package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Map;

@ApiModel("계약서 자재 목록 조회")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractMaterialResponse {

    @ApiModelProperty(value = "자재코드")
    private String materialCode;
    @ApiModelProperty(value = "자재명")
    private String materialValue;

    public static ContractMaterialResponse fromMap(Map<String, String> map) {
        return ContractMaterialResponse.builder()
                .materialCode(map.get("code"))
                .materialValue(map.get("value"))
                .build();
    }
}
