package ru.example.group.main.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.data.ContactConfirmationPayload;
import ru.example.group.main.data.ContactConfirmationResponse;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repositories.JwtBlacklistRepository;
import ru.example.group.main.repositories.SocialNetUserRepository;
import ru.example.group.main.security.SocialNetUserDetails;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class SocialNetUserRegisterService {

    private final SocialNetUserRepository socialNetUserRepository;
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtilService jwtUtilService;

    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    public SocialNetUserRegisterService(SocialNetUserRepository socialNetUserRepository, SocialNetUserDetailsService socialNetUserDetailsService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtilService jwtUtilService, JwtBlacklistRepository jwtBlacklistRepository) {
        this.socialNetUserRepository = socialNetUserRepository;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtilService = jwtUtilService;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public UserEntity registerTestUser() {
            UserEntity user = new UserEntity();
            user.setRegDate(LocalDateTime.now());
            user.setEmail("test@test.tu");
            user.setPassword(passwordEncoder.encode("111"));
            socialNetUserRepository.save(user);
            return user;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(),
                payload.getPassword()));
        SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());

        String jwtToken = jwtUtilService.generateToken(userDetails);

        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        try {
            SocialNetUserDetails userDetails =
                    (SocialNetUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUser();
        } catch (Exception e) {
            return null;
        }
    }



    public void logoutProcessing(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {

                    token = cookie.getValue();
                    JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
                    jwtBlacklistEntity.setJwtBlacklisted(token);
                    jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
                    jwtBlacklistRepository.save(jwtBlacklistEntity);

                    cookie.setMaxAge(0);
                    response.addCookie(cookie);

                    HttpSession session = request.getSession();
                    SecurityContextHolder.clearContext();
                    if (session != null) {
                        session.invalidate();
                    }
                }
            }
        }

        request.logout();
    }
}
