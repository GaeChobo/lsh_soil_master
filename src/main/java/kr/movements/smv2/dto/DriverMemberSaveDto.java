package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@ApiModel("운송기사 앱 - 회원가입 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverMemberSaveDto {

    @Size(max = 255, message = "oauthKey 등록 오류")
    @NotNull(message = "oauth type 오류")
    @ApiModelProperty(value = "provider", example = "kakao")
    private String provider;

    @Size(max = 255, message = "oauthNum 연동 오류")
    @NotNull(message = "oauth id 오류")
    @ApiModelProperty(value = "provider_id", example = "1232321")
    private String providerId;

    @Email
    @ApiModelProperty(value = "oauth 이메일", example = "etet@mo.kr")
    private String email;

    @Size(max = 10, message = "회원명은 10글자까지 입력 가능합니다.")
    @NotNull(message = "회원명을 입력해주세요")
    @ApiModelProperty(value = "회원명", example = "김동하")
    private String userName;

    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", message = "핸드폰 번호 형식으로 입력해야 합니다.")
    @ApiModelProperty(value = "연락처", example = "010-0000-0000")
    private String userPhone;

    @Size(max = 9, message = "차량번호를 확인해주세요")
    @ApiModelProperty(value = "차량번호", example = "서울83아1234")
    private String carNumber;

    @Size(max = 20, message = "회사명은 한글, 영문, 숫자, ()만 가능합니다")
    @ApiModelProperty(value = "운송회사명", example = "무브먼츠")
    private String driverCompanyName;

    @ApiModelProperty(value = "개인정보 약관 동의여부")
    private boolean personalInfoAgree;

    @ApiModelProperty(value = "서비스이용 약관 동의여부")
    private boolean termsOfServiceAgree;

}
