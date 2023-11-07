package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@ApiModel("운송기사 회원가입 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverSaveDto {
//    @ApiModelProperty(value = "회원정보 id")
//    private Long userId;

    @Size(max = 16, message = "간편인증 구분 오류")
    @ApiModelProperty(value = "간편인증 구분", example = "kakao")
    private String snsType;

    @Size(max = 255, message = "인증키 등록 오류")
    @ApiModelProperty(value = "공급자 키(kakao)", example = "1232321")
    private String providerKey;

    @Size(max = 10, message = "회원명은 10글자까지 입력 가능합니다.")
    @ApiModelProperty(value = "oauth 회원명", example = "김동하")
    private String oauthUserName;

    @Size(max = 13, message = "전화번호는 최대 13자리 입니다.")
    @ApiModelProperty(value = "oauth 연락처", example = "010-0000-0000")
    private String oauthUserPhone;

    @Email
    @NotNull
    @ApiModelProperty(value = "oauth 이메일", example = "etet@mo.kr")
    private String oauthUserEmail;

    @Size(max = 10, message = "회원명은 10글자까지 입력 가능합니다.")
    @ApiModelProperty(value = "회원명", example = "김동하")
    private String userName;

    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", message = "핸드폰 번호 형식으로 입력해야 합니다.")
    @ApiModelProperty(value = "연락처", example = "010-0000-0000")
    private String userPhone;

    @Email
    @ApiModelProperty(value = "이메일", example = "tets@mo.kr")
    private String userEmail;

    @Size(max = 255, message = "회원 인증키 등록 오류")
    @ApiModelProperty(value = "회원 토큰", example = "13232142")
    private String userToken;

    //@ApiModelProperty(value = "회원 권한")
    //private String userAuth;

    @Size(max = 12, message = "차량번호를 확인해주세요")
    @ApiModelProperty(value = "차량번호", example = "23누 1232")
    private String carNumber;

    @Size(max = 20, message = "회사명은 20글자까지 입력가능합니다")
    @ApiModelProperty(value = "운송회사명", example = "무브먼츠")
    private String driverCompanyName;

    @ApiModelProperty(value = "개인정보 약관 동의")
    private boolean personalInfoAgree;

    @ApiModelProperty(value = "서비스이용 약관 동의")
    private boolean termsOfServiceAgree;

    @ApiModelProperty(value = "위치정보 수집 동의")
    private boolean gpsAgree;
}