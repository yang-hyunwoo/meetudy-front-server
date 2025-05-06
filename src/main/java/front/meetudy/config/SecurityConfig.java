package front.meetudy.config;

import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.config.jwt.filter.JwtAuthenticationFilter;
import front.meetudy.config.jwt.filter.JwtAuthorizationFilter;
import front.meetudy.oauth.OAuth2AuthenticationFailureHandler;
import front.meetudy.oauth.OAuth2AuthenticationSuccessHandler;
import front.meetudy.oauth.PrincipalOauth2UserService;
import front.meetudy.property.JwtProperty;
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
    private final JwtProcess jwtProcess;
    private final JwtProperty jwtProperty;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthorizationFilter(authenticationManager, memberRepository, jwtProcess, jwtProperty);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, memberService, jwtProcess, jwtProperty);
    }

    /**
     * 운영 환경
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Profile("prod")
    public SecurityFilterChain securityFilterChainProd(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        return buildSecurityFilterChain(http, authenticationManager, false);
    }

    /**
     * 로컬 환경
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Profile("local")
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        return buildSecurityFilterChain(http, authenticationManager, true);
    }

    /**
     * 로컬 환경
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Profile("test")
    public SecurityFilterChain securityFilterChainTest(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        return buildSecurityFilterChain(http, authenticationManager, true);
    }

    private SecurityFilterChain buildSecurityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, boolean isDev) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/**"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthorizationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilter(jwtAuthenticationFilter(authenticationManager))                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint(jwtProperty)))
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()

                );

        if (isDev) {
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        } else {
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny));
        }

        return http.build();
    }
}
