package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * packageName : kr.movements.smv2.dto
 * fileName    : TokenDto
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDto {
    private final Long userId;
    private final String authCode;
    private final String accessToken;
    private final String refreshToken;
    private final Boolean verification;

    public TokenDto(Long userId, String authCode, String accessToken, String refreshToken, Boolean verification) {
        this.userId = userId;
        this.authCode = authCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.verification = verification;
    }
}
