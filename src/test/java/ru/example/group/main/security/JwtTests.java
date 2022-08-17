package ru.example.group.main.security;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.entity.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.yml")
public class JwtTests {

    private final static String EMAIL = "test@test.tu";
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final JWTUtilService jwtUtilService;

    @Autowired
    public JwtTests(SocialNetUserRegisterService socialNetUserRegisterService, SocialNetUserDetailsService socialNetUserDetailsService, JWTUtilService jwtUtilService) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.jwtUtilService = jwtUtilService;
    }

    @Test
    void jwtLogin() {
        ContactConfirmationPayloadDto payload = new ContactConfirmationPayloadDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertNotNull(userDetails);
        assertTrue(jwtUtilService.validateToken(socialNetUserRegisterService.jwtLogin(payload, null, null).getData().getToken(), userDetails));
    }

    @Test
    void jwtTokenValidation() {
        ContactConfirmationPayloadDto payload = new ContactConfirmationPayloadDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertNotNull(userDetails);
        assertTrue(jwtUtilService.validateToken(jwtUtilService.generateToken(userDetails), userDetails));
    }

    @Test
    void jwtTokenValidationAndExpiredRegisterServiceAndUtilService() {
        ContactConfirmationPayloadDto payload = new ContactConfirmationPayloadDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertTrue(jwtUtilService.validateToken(socialNetUserRegisterService.jwtLogin(payload, null, null).getData().getToken(), userDetails));
        assertTrue(!jwtUtilService.isTokenExpired(socialNetUserRegisterService.jwtLogin(payload, null, null).getData().getToken()));
    }

    @Test
    void jwtGenerateToken() {
        ContactConfirmationPayloadDto payload = new ContactConfirmationPayloadDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
          assertTrue(jwtUtilService.validateToken(jwtUtilService.generateToken(userDetails), userDetails));
    }

    @Test
    void loadUserByUsernameDetailsService(){
        UserDetails user = socialNetUserDetailsService.loadUserByUsername(EMAIL);
        assertNotNull(user);
    }



}