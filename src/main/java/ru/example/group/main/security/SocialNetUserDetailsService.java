package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controller.GlobalExceptionHandlerController;

import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.repository.SocialNetUserRepository;

@Service
public class SocialNetUserDetailsService implements UserDetailsService {

    private final SocialNetUserRepository socialNetUserRepository;
    private final GlobalExceptionHandlerController handlerController;

    @Autowired
    public SocialNetUserDetailsService(SocialNetUserRepository socialNetUserRepository, GlobalExceptionHandlerController handlerController) {
        this.socialNetUserRepository = socialNetUserRepository;
        this.handlerController = handlerController;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity user = socialNetUserRepository.findUserEntityByEmail(s);
        if (user != null) {
            return new SocialNetUserDetails(user);
        }
        handlerController.handleUsernameNotFoundException(new UsernameNotFoundException("user not found doh!"));
        return null;
    }

    public UserDataResponseDto setUserDataResponseDto(UserEntity user, String token) {
        return UserDataResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .regDate(user.getRegDate())
                .birthDate(user.getBirthDate())
                .eMail(user.getEmail())
                .phone(user.getPhone())
                .photo(user.getPhoto())
                .about(user.getAbout())
                .city(user.getCity())
                .country(user.getCountry())
                .messagePermissions(MessagesPermission.getFromBoolean(user.isMessagePermissions()))
                .lastOnlineTime(user.getLastOnlineTime())
                .isBlocked(user.isBlocked())
                .isDeleted(user.isDeleted())
                .token(token)
                .build();
    }



    public UserDataResponseDto setUserDataResponseDto(UserEntity user) {
        return UserDataResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .regDate(user.getRegDate())
                .birthDate(user.getBirthDate())
                .eMail(user.getEmail())
                .phone(user.getPhone())
                .photo(user.getPhoto())
                .about(user.getAbout())
                .city(user.getCity())
                .country(user.getCountry())
                .messagePermissions(MessagesPermission.getFromBoolean(user.isMessagePermissions()))
                .lastOnlineTime(user.getLastOnlineTime())
                .isBlocked(user.isBlocked())
                .isDeleted(user.isDeleted())
                .build();
    }
}
