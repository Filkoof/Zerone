package ru.example.group.main.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;

import static org.junit.jupiter.api.Assertions.*;

class JwtTests extends AbstractAllTestH2ContextLoad {

    private final static String EMAIL = "test@test.tu";
    @Autowired
    private SocialNetUserRegisterService socialNetUserRegisterService;
    @Autowired
    private SocialNetUserDetailsService socialNetUserDetailsService;
    @Autowired
    private JWTUtilService jwtUtilService;

    @Test
    void jwtLogin() {
        ContactConfirmationPayloadRequestDto payload = new ContactConfirmationPayloadRequestDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertNotNull(userDetails);
        assertTrue(jwtUtilService.validateToken(socialNetUserRegisterService.jwtLogin(payload).getData().getToken(), userDetails));
    }

    @Test
    void jwtTokenValidation() {
        ContactConfirmationPayloadRequestDto payload = new ContactConfirmationPayloadRequestDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertNotNull(userDetails);
        assertTrue(jwtUtilService.validateToken(jwtUtilService.generateToken(userDetails), userDetails));
    }

    @Test
    void jwtTokenValidationAndExpiredRegisterServiceAndUtilService() {
        ContactConfirmationPayloadRequestDto payload = new ContactConfirmationPayloadRequestDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertTrue(jwtUtilService.validateToken(socialNetUserRegisterService.jwtLogin(payload).getData().getToken(), userDetails));
        assertFalse(jwtUtilService.isTokenExpired(socialNetUserRegisterService.jwtLogin(payload).getData().getToken()));
    }

    @Test
    void jwtGenerateToken() {
        ContactConfirmationPayloadRequestDto payload = new ContactConfirmationPayloadRequestDto();
        payload.setPassword("11111111");
        payload.setEmail(EMAIL);
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
        assertTrue(jwtUtilService.validateToken(jwtUtilService.generateToken(userDetails), userDetails));
    }

    @Test
    void loadUserByUsernameDetailsService() {
        UserDetails user = socialNetUserDetailsService.loadUserByUsername(EMAIL);
        assertNotNull(user);
    }


}
