package kr.movements.smv2.config;

import kr.movements.smv2.auth.service.PrincipalOauth2UserService;
import kr.movements.smv2.auth.util.CookieAuthorizationRequestRepository;
import kr.movements.smv2.common.filter.JwtExceptionFilter;
import kr.movements.smv2.common.filter.JwtFilter;
import kr.movements.smv2.common.filter.OAuth2AuthenticationFailureHandler;
import kr.movements.smv2.common.filter.OAuth2AuthenticationSuccessHandler;
import kr.movements.smv2.entity.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Optional;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements AuditorAware<Long> {
    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    private final PrincipalOauth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private static final String[] AUTH_LIST = {
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger/**",
            "/guest/**",
            "/driver/waybill/location",
            "/driver/member/sms/**",
            "/driver/member/sms-receive",
            "/driver/member/policy/**",
            "/auth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .rememberMe().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/driver/**").hasAuthority("1030")
                .antMatchers("/site-manager/**").hasAuthority("1020")
                .antMatchers("/admin/**").hasAuthority("1010")
                .antMatchers(AUTH_LIST).permitAll(); // 해당 url 들은 모두 허용
/*
        http
            .formLogin(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo //로그인 성공 후 사용자정보를 가져온다 //OAuth 2.0 Provider로부터 사용자 정보를 가져오는 엔드포인트를 지정하는 메서드
                                .userService(customOAuth2UserService)//oauth2 사용자정보 인증 처리할 때 사용한다
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler) //인증 성공 후 redirect 할 목적
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

 */

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // jwt 필터를 등록
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class);

        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Spring Security 에 의한 filter 를 거치지 않고 전부 허용함. (주로 정적파일을 등록해야함)
        return (web) -> web.ignoring().antMatchers(AUTH_LIST);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 1시간동안 pre-flight request(cors 예비요청)를 저장하고 캐싱
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object authentic = authentication.getPrincipal();
        if(authentic.equals("anonymousUser")){ //swagger 사용자
            return Optional.empty();
        }
        User user = (User) authentication.getPrincipal();
//        return authentication == null || !authentication.isAuthenticated() ? Optional.empty() : Optional.of(authentication.getName());
        return !authentication.isAuthenticated() ? Optional.of(1L) : Optional.of(Long.valueOf(user.getUsername()));
    }
}


