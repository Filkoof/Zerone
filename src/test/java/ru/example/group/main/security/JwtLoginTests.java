package ru.example.group.main.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JwtLoginTests {

    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final JWTUtilService jwtUtilService;

    @Autowired
    public JwtLoginTests(SocialNetUserRegisterService socialNetUserRegisterService, SocialNetUserDetailsService socialNetUserDetailsService, JWTUtilService jwtUtilService) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.jwtUtilService = jwtUtilService;
    }

    @Test
    void jwtLogin() {
        ContactConfirmationPayloadDto payload = new ContactConfirmationPayloadDto();
        payload.setPassword("11111111");
        payload.setEmail("admin@admin.tu");
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());

        assertTrue(jwtUtilService.validateToken(socialNetUserRegisterService.jwtLogin(payload).getData().getToken(), userDetails));
    }
}
