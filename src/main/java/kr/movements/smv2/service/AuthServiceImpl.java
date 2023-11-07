package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.exception.UnauthorizedException;
import kr.movements.smv2.common.util.TokenProvider;
import kr.movements.smv2.dto.LoginDto;
import kr.movements.smv2.dto.OAuthLoginDto;
import kr.movements.smv2.dto.RefreshDto;
import kr.movements.smv2.dto.TokenDto;
import kr.movements.smv2.entity.DriverOauthEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.DriverOauthRepository;
import kr.movements.smv2.repository.SiteManagerRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import kr.movements.smv2.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

/**
 * packageName : kr.movements.smv2.service
 * fileName    : LoginService
 * author      : ckr
 * date        : 2023/05/06
 * description :
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserInfoRepository userInfoRepository;
    private final DriverOauthRepository driverOauthRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final UserVerificationRepository userVerificationRepository;

    private final InMemoryClientRegistrationRepository inMemoryRepository;

    @Transactional
    public TokenDto login(LoginDto loginDto){
        UserInfoEntity userInfo = userInfoRepository.findByUserEmailAndHasDeleted(loginDto.getEmail(), false).orElseThrow(() -> new BadRequestException("회원아이디 또는 비밀번호가 일치하지 않습니다."));
        if(!passwordEncoder.matches(loginDto.getPassword(), userInfo.getUserPw())){
            throw new BadRequestException("회원아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);

        String accessToken = tokenProvider.createToken(userInfo.getId(), authentication, true);
        String refreshToken = "";

        //refresh 토큰이 존재하지 않을 경우 발급
        if(!StringUtils.hasText(userInfo.getUserToken())) {
            refreshToken = tokenProvider.createToken(userInfo.getId(), authentication, false);
            userInfo.updateToken(refreshToken);
        }else { //토큰이 있으면 발급 안함. 토큰이 안맞을 경우는 refresh api 콜
            refreshToken = userInfo.getUserToken();
        }

        //현장관리자 - 이메일 인증여부 확인
        boolean verification = userVerificationRepository.existsByUserIdAndEmailAndAndVerificationStatus(userInfo, loginDto.getEmail(), true);

        return new TokenDto(userInfo.getId(), userInfo.getUserAuthCode(), accessToken, refreshToken,
                userInfo.getUserTypeCode().equals(CommonCode.AUTH_SITE.getCode()) ? verification : null);
    }

    @Override
    public TokenDto oAuthLogin(OAuthLoginDto oAuthLoginDto) {
//        DriverOauthEntity driverOauthEntity = driverOauthRepository.findByProviderIdAndProviderAndEmail(oAuthLoginDto.getProviderId(), oAuthLoginDto.getProvider(), oAuthLoginDto.getEmail()).orElseThrow(() -> new BadRequestException("oauth 인증 정보를 확인할 수 없습니다."));
        DriverOauthEntity driverOauthEntity = driverOauthRepository.findByProviderIdAndProviderAndEmailAndHasDeleted(oAuthLoginDto.getProviderId(), oAuthLoginDto.getProvider(), oAuthLoginDto.getEmail(), false).orElseThrow(() -> new BadRequestException("oauth 인증 정보를 확인할 수 없습니다."));

        if(driverOauthEntity.getUserInfoId() == null) {
            //회원가입 요청 전에 사용중인 이메일인지 확인
            if(userInfoRepository.existsByUserEmailAndHasDeleted(oAuthLoginDto.getEmail(), false)){
                throw new BadRequestException("가입하려는 이메일은 이미 사용중 입니다.");
            }
            throw new BadRequestException("가입된 회원정보가 없습니다.");
        }
        UserInfoEntity userInfo = userInfoRepository.findById(driverOauthEntity.getUserInfoId()).orElseThrow(() -> new BadRequestException("사용자 정보를 확인할 수 없습니다."));

        //사용자 정보 인증 후 토큰 발급
        //비밀번호 평문. Authentication 에 평문으로 들어가야 작동함
        String password = oAuthLoginDto.getProvider() + "_" + oAuthLoginDto.getProviderId() + "_" + oAuthLoginDto.getEmail();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(oAuthLoginDto.getEmail(), password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);

        String accessToken = tokenProvider.createToken(userInfo.getId(), authentication, true);
        String refreshToken = tokenProvider.createToken(userInfo.getId(), authentication, false);
        userInfo.updateToken(refreshToken);

        return new TokenDto(userInfo.getId(), userInfo.getUserAuthCode(), accessToken, refreshToken, null);
    }

    @Override
    public TokenDto refresh(RefreshDto refreshDto) {
        UserInfoEntity userInfo = userInfoRepository.findByIdAndHasDeleted(refreshDto.getUserId(), false).orElseThrow(() -> new BadRequestException("아이디가 존재하지 않거나 삭제되었습니다."));
        if(userInfo.getUserToken().equals(refreshDto.getRefreshToken())){
            //토큰 유효한지 검증
            tokenProvider.validateToken(refreshDto.getRefreshToken(), false);
            //인증객체
            Authentication authentication = tokenProvider.getAuthentication(refreshDto.getRefreshToken(), false);
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo.getUserEmail(), null);
//            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);

            //리프레시 재발급(만료 7일 내에 접속 시)

            //현장관리자 - 이메일 인증여부 확인
            boolean verification = userVerificationRepository.existsByUserIdAndEmailAndAndVerificationStatus(userInfo, userInfo.getUserEmail(), true);

            //새로운 엑세스 토큰 발급
            return new TokenDto(userInfo.getId(), userInfo.getUserAuthCode(), tokenProvider.createToken(userInfo.getId(), authentication, true), null,
                    userInfo.getUserTypeCode().equals(CommonCode.AUTH_SITE.getCode()) ? verification : null);
        } else {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        log.info("loadUserByUsername: ", userEmail);
        UserInfoEntity userInfo = userInfoRepository.findByUserEmailAndHasDeleted(userEmail, false).orElseThrow(() -> new UnauthorizedException("해당 email의 회원이 삭제되었거나 존재하지 않습니다."));

        return User.builder()
            .username(userInfo.getUserEmail())
            .password(userInfo.getUserPw())
            .authorities(Collections.singleton(new SimpleGrantedAuthority(userInfo.getUserAuthCode())))
            .build();
    }

}
