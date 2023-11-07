package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("이용약관 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreeTermsConditionsDTO {

    @ApiModelProperty(value = "약관 id")
    private Long policyId;

    @ApiModelProperty(value = "약관 제목")
    private String privateTitle;

}
