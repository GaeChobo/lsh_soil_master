package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class OAuthLoginDto {
    @NotBlank
    @ApiModelProperty(value = "provider_id", example = "142321")
    private String providerId;
    @NotBlank
    @ApiModelProperty(value = "provider", example = "kakao")
    private String provider;
    @NotBlank
    @ApiModelProperty(value = "email", example = "akj14291@gmail.com")
    private String email;
}
