package kr.movements.smv2.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("QR 불통과 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrNotPassDto {

    @ApiModelProperty(value = "송장 ID", example = "1352435123412")
    private Long waybillId;
    @ApiModelProperty(value = "송장 인증 PW", example = "1234")
    private String certificationPw;
    @ApiModelProperty(value = "사유 입력", example = "직접입력 시 사용")
    private String reason;
    @ApiModelProperty(value = "qrcode 사용여부", example = "true")
    private Boolean qrcodeUse;
    @ApiModelProperty(value = "불통과 사유 타입", example = "7010 : 불량, 7020 : 직접입력")
    private String notPasscode;
}
