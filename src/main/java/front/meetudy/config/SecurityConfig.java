package front.meetudy.config;

import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.config.jwt.filter.JwtAuthorizationFilter;
import front.meetudy.user.oauth.OAuth2AuthenticationFailureHandler;
import front.meetudy.user.oauth.OAuth2AuthenticationSuccessHandler;
import front.meetudy.user.oauth.PrincipalOauth2UserService;
import front.meetudy.property.FrontJwtProperty;
import front.meetudy.user.repository.member.MemberRepository;
import front.meetudy.security.handler.CustomLogOutHandler;
import front.meetudy.user.service.redis.RedisService;
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
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtProcess jwtProcess;
    private final FrontJwtProperty frontJwtProperty;
    private final RedisService redisService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthorizationFilter(authenticationManager, memberRepository, jwtProcess, frontJwtProperty,redisService);
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
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthorizationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint(frontJwtProperty)))
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .logout(logout-> logout.logoutUrl("/api/logout").logoutSuccessHandler(new CustomLogOutHandler(jwtProcess,redisService)))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/private/**","/api/user/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/login"
                        ).permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                );
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
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
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint(frontJwtProperty)))
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .logout(logout-> logout.logoutUrl("/api/logout").logoutSuccessHandler(new CustomLogOutHandler(jwtProcess,redisService)))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/login"
                        ).permitAll()
                        .requestMatchers("/api/private/**","/api/user/**").authenticated()
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
