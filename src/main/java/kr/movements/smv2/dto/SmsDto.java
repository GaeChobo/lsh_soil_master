package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * packageName : kr.movements.smv2.dto
 * fileName    : SmsDto
 * author      : ckr
 * date        : 2023/05/07
 * description :
 */

@Getter
public class SmsDto {
    // - 제외한 숫자만 받아야함.
    @NotNull
    @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$", message = "핸드폰 번호 숫자만 입력해주세요")
    @ApiModelProperty(value = "연락처", example = "01012345678")
    private String tel;
}
