package kr.movements.smv2.service;

import kr.movements.smv2.dto.LoginDto;
import kr.movements.smv2.dto.OAuthLoginDto;
import kr.movements.smv2.dto.RefreshDto;
import kr.movements.smv2.dto.TokenDto;

/**
 * packageName : kr.movements.smv2.service
 * fileName    : LoginService
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */
public interface AuthService {

    TokenDto login(LoginDto loginDto);
    TokenDto oAuthLogin(OAuthLoginDto oAuthLoginDto);
//    TokenDto loginKakao(String kakaoNumber);
//    LoginAppResponse loginKakao(String providerName, String code);
    TokenDto refresh(RefreshDto refreshDto);
}
