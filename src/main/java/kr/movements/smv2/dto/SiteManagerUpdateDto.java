package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@ApiModel("어드민 - 현장관리자 정보수정 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteManagerUpdateDto {

    @ApiModelProperty(value = "회원 회원명", example = "윤대훈")
    private String userName;
    @ApiModelProperty(value = "회원 연락처", example = "010-1234-5678")
    private String userPhone;
    @ApiModelProperty(value = "회원 이메일", example = "asd@movements.kr")
    private String userEmail;
    @ApiModelProperty(value = "현장관리자 계근대여부")
    private boolean weighBridgeType;
    @ApiModelProperty(value = "현장관리자 인증번호", example = "'1231515626'")
    private String siteCertificationPw;
    @ApiModelProperty(value = "현장관리자 qr신규발급 여부", example = "true")
    private boolean newQrCodeUse;

}
