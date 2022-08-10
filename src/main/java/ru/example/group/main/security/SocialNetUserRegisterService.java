package ru.example.group.main.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.dto.*;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;
import ru.example.group.main.repository.SocialNetUserRepository;
import ru.example.group.main.repository.UserRoleEntityRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class SocialNetUserRegisterService {

    @Value("${header.authorization}")
    private String authHeader;
    private final SocialNetUserRepository socialNetUserRepository;
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtilService jwtUtilService;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final Logger logger = Logger.getLogger(SocialNetUserRegisterService.class.getName());

    @Autowired
    public SocialNetUserRegisterService(SocialNetUserRepository socialNetUserRepository, SocialNetUserDetailsService socialNetUserDetailsService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtilService jwtUtilService, JwtBlacklistRepository jwtBlacklistRepository, UserRoleEntityRepository userRoleEntityRepository) {
        this.socialNetUserRepository = socialNetUserRepository;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtilService = jwtUtilService;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.userRoleEntityRepository = userRoleEntityRepository;
    }

    public UserEntity registerTestUser() {
        UserEntity user = new UserEntity();
        user.setRegDate(LocalDateTime.now());
        user.setEmail("test@test.tu");
        user.setPassword(passwordEncoder.encode("11111111"));
        socialNetUserRepository.save(user);

        user = new UserEntity();
        user.setRegDate(LocalDateTime.now());
        user.setEmail("admin@admin.tu");
        user.setPassword(passwordEncoder.encode("11111111"));
        socialNetUserRepository.save(user);

        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserRole("ROLE_ADMIN");
        userRoleEntity.setUserForRole(user);
        userRoleEntity.setUserId(user.getId());
        userRoleEntityRepository.save(userRoleEntity);

        return user;
    }

    public CommonResponseDto<UserLoginDataResponseDto> jwtLogin(ContactConfirmationPayloadDto payload) {
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserLoginDataResponseDto>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(),
                    payload.getPassword()));
            SocialNetUserDetails userDetails =
                    (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
            authLoginResponseDto = setAuthLoginResponse(userDetails);
        } catch (Exception e) {
            logger.info("jwtLogin " + e.getMessage());
            authLoginResponseDto.setError("Неверные данные учетной записи.");
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        }
        return authLoginResponseDto;
    }

    private CommonResponseDto<UserLoginDataResponseDto> setAuthLoginResponse(SocialNetUserDetails userDetails) {
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserLoginDataResponseDto>();
        ContactConfirmationResponseDto response = new ContactConfirmationResponseDto();
        if (!userDetails.getUser().isApproved()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("Пользователь еще не подтвержден администратором.");
            return authLoginResponseDto;
        }
        if (userDetails.getUser().isBlocked()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("Пользователь заблокирован.");
            return authLoginResponseDto;
        }
        String jwtToken = null;
        jwtToken = jwtUtilService.generateToken(userDetails);
        response.setResult(jwtToken);
        response.setUserDto(socialNetUserDetailsService.setUserDtoFromAuth(userDetails.getUser(), jwtToken));
        authLoginResponseDto.setData(response.getUserDto());
        authLoginResponseDto.setError("");
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        return authLoginResponseDto;
    }

    public UserEntity getCurrentUser() {
        try {
            SocialNetUserDetails userDetails =
                    (SocialNetUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUser();
        } catch (Exception e) {
            return null;
        }
    }

}
