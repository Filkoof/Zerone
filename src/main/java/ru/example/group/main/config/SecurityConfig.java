package ru.example.group.main.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.example.group.main.security.JWTRequestFilter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTRequestFilter filter;
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .antMatchers("/api/v1/auth/*", "/api/v1/auth/logout" ,"/auth/api/logout","/api/v1/account/register", "/api/v1/account/recovery", "/api/v1/account/register/confirm", "/admin").permitAll()
                .antMatchers("/api/v1/account/registration_complete/*", "/api/v1/account/recovery_complete", "/api/v1/support").permitAll()
                .antMatchers("/api/v1/platform/languages", "/email_change/confirm", "/password_change/confirm", "/api/v1/account/password/set", "/user_delete/confirm").permitAll()
                .antMatchers("/user_delete_recovery/confirm").permitAll()
                .antMatchers("/api/v1/platform/languages", "/email_change/confirm", "/password_change/confirm", "/api/v1/account/password/set").permitAll()
                .antMatchers("/swagger-resources/**", "/v2/**", "/swagger-ui", "/swagger-ui/**").permitAll()
                .antMatchers( "**").authenticated()
                .and()
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .permitAll()
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value())
                        )
                )
                .exceptionHandling(exh -> exh.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
