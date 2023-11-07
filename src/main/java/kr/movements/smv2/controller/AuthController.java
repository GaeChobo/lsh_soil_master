package kr.movements.smv2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.auth.template.PrincipalDetails;
import kr.movements.smv2.common.util.RegexUtil;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.service.AuthService;
import kr.movements.smv2.service.DriverMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * packageName : kr.movements.smv2.controller
 * fileName    : AuthController
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Auth")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final DriverMemberService driverMemberService;

    @ApiOperation(value = "로그인")
    @PostMapping(value = "/login")
    public Payload<TokenDto> login(@Valid @RequestBody LoginDto loginDto,
                                   HttpServletRequest request, HttpServletResponse response) {
        return new Payload<>( HttpStatus.OK, request.getServletPath(), authService.login(loginDto), response);
    }


    @ApiOperation(value = "oauth2 로그인")
    @PostMapping("/oauth/login")
    public Payload<TokenDto> oauthLogin(@Valid @RequestBody OAuthLoginDto oAuthLoginDto,HttpServletRequest request, HttpServletResponse response){
        return new Payload<>( HttpStatus.OK, request.getServletPath(), authService.oAuthLogin(oAuthLoginDto), response);
    }


    @ApiOperation(value = "액세스 토큰 갱신")
    @PostMapping(value = "/refresh")
    public Payload<TokenDto> refresh(@Valid @RequestBody RefreshDto refreshDto,
                                     HttpServletRequest request, HttpServletResponse response) {
        return new Payload<>( HttpStatus.OK, request.getServletPath(), authService.refresh(refreshDto), response);
    }

    @ApiOperation(value = "운송기사 회원가입")
    @PostMapping(value = "/driver/member/create")
    public Payload<String> driverCreate(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @RequestBody @Valid DriverMemberSaveDto dto) {

        driverMemberService.driverMemberSave(dto);
        return new Payload<>(HttpStatus.OK, request.getServletPath(), response);
    }

    @ApiOperation(value = "차량번호 정규식 테스트")
    @PostMapping(value = "/driver/car")
    public Payload<String> driverCreate(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @RequestParam String carNumber) {

        RegexUtil regexUtil = new RegexUtil();
        regexUtil.carNumCheck(carNumber);
        return new Payload<>(HttpStatus.OK, request.getServletPath(), response);
    }
}
