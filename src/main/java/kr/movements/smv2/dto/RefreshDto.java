package kr.movements.smv2.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * packageName : kr.movements.smv2.dto
 * fileName    : RefreshDto
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */

@Getter
public class RefreshDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String refreshToken;
}
