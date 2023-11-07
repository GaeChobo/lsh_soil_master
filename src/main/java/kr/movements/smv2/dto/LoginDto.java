package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * packageName : kr.movements.smv2.dto
 * fileName    : LoginDto
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */

@Getter
public class LoginDto {

    @NotBlank
    @ApiModelProperty(value = "이메일", example = "rhanfwn0996@naver.com")
    private String email;
    @NotBlank
    @ApiModelProperty(value = "비밀번호", example = "mv.asd")
    private String password;
}
