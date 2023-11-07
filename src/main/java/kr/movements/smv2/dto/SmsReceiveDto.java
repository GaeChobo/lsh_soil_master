package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SmsReceiveDto {
    @NotNull
    @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$", message = "핸드폰 번호 숫자만 입력해주세요")
    @ApiModelProperty(value = "연락처", example = "01012345678")
    private String tel;
    @NotNull
    @Size(min = 5, max = 5, message = "인증번호는 5자리 입니다")
    @ApiModelProperty(value = "인증번호", example = "12345")
    private String smsKey;
}
