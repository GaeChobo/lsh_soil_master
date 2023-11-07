package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("송장 불통과 인증dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotPassMatchDto {

    @NotNull
    @ApiModelProperty(value = "송장 ID")
    private Long waybillId;

    @NotNull
    @NotBlank
    @ApiModelProperty(value = "인증번호 or Qr 데이터")
    private String certificationPw;

    @NotNull
    @ApiModelProperty(value = "Qr 사용유무")
    Boolean qrcodeUse;
}
