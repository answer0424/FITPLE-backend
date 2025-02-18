package com.lec.spring.base.config;

import com.lec.spring.base.config.oauth.CustomOauth2SuccessHandler;
import com.lec.spring.base.config.oauth.PrincipalOauth2UserService;
import com.lec.spring.base.jwt.JWTFilter;
import com.lec.spring.base.jwt.JWTUtil;
import com.lec.spring.base.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;

    private final PrincipalOauth2UserService principalOauth2UserService;

    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, PrincipalOauth2UserService principalOauth2UserService, CustomOauth2SuccessHandler customOauth2SuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.principalOauth2UserService = principalOauth2UserService;
        this.customOauth2SuccessHandler = customOauth2SuccessHandler;
    }

    @Value("${cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // csrf disable
        http.csrf(auth -> auth.disable());

        // Form 인증 방식 disable
        http.formLogin(auth -> auth.disable());

        // http basic 인증 방식 disable
        http.httpBasic(auth -> auth.disable());

        // 경로별 인가 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/send-reset-email").permitAll()
                        .requestMatchers("/reset-password").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/api/hbti/data").permitAll()
                        .requestMatchers("/api/hbti/save").permitAll()
                        .requestMatchers("/member/send-reset-email").permitAll()
                        .requestMatchers("/member/reset-password").permitAll()
                        .requestMatchers("/member/detail").permitAll()
                        .requestMatchers("member/detail/write").permitAll()
                        .requestMatchers("/member/calendar/delete-schedule/**").permitAll()
                        .requestMatchers("/api/hbti/calculate").permitAll()
                        .requestMatchers("/api/hbti/type/*").permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/upload/**").permitAll()
                        .requestMatchers("/api/reviews/**").authenticated()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/quiz/**").permitAll()
                        .requestMatchers("/api/quiz/search").permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()
                        .requestMatchers("/member/**").authenticated()
                        .requestMatchers("/member/register/add-schedule/{userId}").hasRole("Trainer")
                        .requestMatchers("/member/{userId}/register/search").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/member/{userid}/calendar/student/{studentId}").authenticated()

                        .anyRequest().authenticated());

        // 세션 설정
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        // LoginFilter 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // CORS 설정
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(corsAllowedOrigins);
                        configuration.setAllowedMethods(List.of("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(List.of("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(List.of("Authorization"));

                        return configuration;
                    }
                }));

        // oauth2 설정
        http
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService))
                        .successHandler(customOauth2SuccessHandler)
                );

        return http.build();

    }


}
