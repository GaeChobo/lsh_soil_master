package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("현장관리자 - 내 정보수정 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteManagerPwdUpdateDto {

    @ApiModelProperty(value = "유저 ID")
    private Long userId;
    @ApiModelProperty(value = "현장관리자 현재비밀번호", example = "내 정보수정하기 전이면 이메일")
    private String originPassword;
    @ApiModelProperty(value = "현장관리자 새 비밀번호", example = "8~16자 영문 소문자 , 숫자")
    private String changedPassword;

}
