package ru.example.group.main.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.example.group.main.security.JWTRequestFilter;


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
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/*", "/api/v1/auth/logout" ,"/auth/api/logout","/api/v1/account/register", "/api/v1/account/recovery", "/api/v1/account/register/confirm").permitAll()
                .antMatchers("/api/v1/account/registration_complete/*", "/api/v1/account/recovery_complete", "/api/v1/support").permitAll()
                .antMatchers("/api/v1/platform/languages", "/email_change/confirm", "/password_change/confirm", "/api/v1/account/password/set", "/user_delete/confirm").permitAll()
                .antMatchers("/user_delete_recovery/confirm").permitAll()
                .antMatchers( "**").authenticated()
                .and().formLogin();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
