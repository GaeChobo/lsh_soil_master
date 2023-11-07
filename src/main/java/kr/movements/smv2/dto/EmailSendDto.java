package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@ApiModel("이메일 보내기 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSendDto {

    @Email
    @ApiModelProperty(value = "이메일", example = "lsh.mv@movements.kr")
    private String email;

    @ApiModelProperty(value = "유저 ID")
    private Long userId;
}
