package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("QR 통과 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrMatchDto {

    @ApiModelProperty(value = "송장ID", example = "1352435123412")
    private Long waybillId;
    @ApiModelProperty(value = "송장인증PW", example = "'1234'")
    private String certificationPw;
    @ApiModelProperty(value = "qrcode사용여부", example = "true")
    private Boolean qrcodeUse;

}
