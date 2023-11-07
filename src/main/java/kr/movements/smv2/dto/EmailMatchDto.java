package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("이메일 인증 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMatchDto {

    @ApiModelProperty(value = "유저 ID")
    private Long userId;

    @ApiModelProperty(value = "인증코드", example = "123456")
    private String verificationCode;
}
