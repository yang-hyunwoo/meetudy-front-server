package front.meetudy.config;

import front.meetudy.config.jwt.filter.JwtAuthenticationFilter;
import front.meetudy.config.jwt.filter.JwtAuthorizationFilter;
import front.meetudy.oauth.OAuth2AuthenticationFailureHandler;
import front.meetudy.oauth.OAuth2AuthenticationSuccessHandler;
import front.meetudy.oauth.PrincipalOauth2UserService;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.member.MemberService;
import front.meetudy.util.CustomAccessDeniedHandler;
import front.meetudy.util.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain securityFilterChainProd(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));  //AuthenticationManager 수동으로 가져옴
        return http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)) // 보안 최고
                .csrf(AbstractHttpConfigurer::disable) //enalbed 일 경우 post맨이 작동하지 않음 , next.js + jwt 사용 하므로 disable
                .cors(cors -> cors.configurationSource(corsConfig.configurationSource()))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //자바의 세션을 사용X JWT 사용
                .formLogin(AbstractHttpConfigurer::disable) //시큐리티의 폼로그인을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable) //브라우저가 팝업창을 이용하여 사용자 인증을 진행하지 않음
                .oauth2Login(oauth2Login -> oauth2Login.authorizationEndpoint(authorizationEndpoint-> authorizationEndpoint
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirectionEndpoint-> redirectionEndpoint.baseUri("/login/oauth2/code/**"))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilter(new JwtAuthenticationFilter(authenticationManager,memberService))
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, memberRepository), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .authorizeHttpRequests(authorizeHttpRequests ->                 // /api/** 에 접근 시 권한이 없다면 접근을 불가능하게 하겠다.
                        authorizeHttpRequests
                                .requestMatchers("/api/user/**")
                                .authenticated())
                .authorizeHttpRequests(authorizeHttpRequests ->                 // api/admin/** 에 접근 시 권한이 ADMIN이 아니라면 접근을 불가능하게 하겠다.
                        authorizeHttpRequests
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN"))
                .authorizeHttpRequests(authorizeHttpRequests ->                 //그 외의 URL에 관한 접근은 모두 허용 하겠다.
                        authorizeHttpRequests
                                .anyRequest()
                                .permitAll()).build();
    }


    @Bean
    @Profile("dev")
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));  //AuthenticationManager 수동으로 가져옴
        return http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // h2-console용
                .csrf(AbstractHttpConfigurer::disable) //enalbed 일 경우 post맨이 작동하지 않음 , next.js + jwt 사용 하므로 disable
                .cors(cors -> cors.configurationSource(corsConfig.configurationSource()))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //자바의 세션을 사용X JWT 사용
                .formLogin(AbstractHttpConfigurer::disable) //시큐리티의 폼로그인을 사용하지 않겠음
                .httpBasic(AbstractHttpConfigurer::disable) //브라우저가 팝업창을 이용하여 사용자 인증을 진행하지 않음
                .oauth2Login(oauth2Login -> oauth2Login.authorizationEndpoint(authorizationEndpoint-> authorizationEndpoint
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirectionEndpoint-> redirectionEndpoint.baseUri("/login/oauth2/code/**"))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilter(new JwtAuthenticationFilter(authenticationManager,memberService))
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, memberRepository), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .authorizeHttpRequests(authorizeHttpRequests ->                 // /api/** 에 접근 시 권한이 없다면 접근을 불가능하게 하겠다.
                        authorizeHttpRequests
                                .requestMatchers("/api/user/**")
                                .authenticated())
                .authorizeHttpRequests(authorizeHttpRequests ->                 // api/admin/** 에 접근 시 권한이 ADMIN이 아니라면 접근을 불가능하게 하겠다.
                        authorizeHttpRequests
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN"))
                .authorizeHttpRequests(authorizeHttpRequests ->                 //그 외의 URL에 관한 접근은 모두 허용 하겠다.
                        authorizeHttpRequests
                                .anyRequest()
                                .permitAll()).build();
    }




}
