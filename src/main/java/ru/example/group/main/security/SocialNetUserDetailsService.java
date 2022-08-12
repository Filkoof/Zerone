package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controller.GlobalExceptionHandlerController;
import ru.example.group.main.dto.UserLoginDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.dto.enumerated.MessagesPermission;
import ru.example.group.main.repository.SocialNetUserRepository;

@Service
public class SocialNetUserDetailsService implements UserDetailsService {

    private final SocialNetUserRepository socialNetUserRepository;
    private GlobalExceptionHandlerController handlerController;

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
        throw new UsernameNotFoundException("user not found doh!");
    }

    public UserLoginDataResponseDto setUserDtoFromAuth(UserEntity user, String token) {
        UserLoginDataResponseDto userLoginDataResponseDto = new UserLoginDataResponseDto();

        userLoginDataResponseDto.setAbout(user.getAbout());
        userLoginDataResponseDto.setBirthDate(user.getBirthDate());
        userLoginDataResponseDto.setBlocked(user.isBlocked());
        userLoginDataResponseDto.setCity(user.getCity());
        userLoginDataResponseDto.setCountry(user.getCountry());
        userLoginDataResponseDto.setPassword(null);
        userLoginDataResponseDto.setRegDate(user.getRegDate());
        userLoginDataResponseDto.setDeleted(user.isDeleted());
        userLoginDataResponseDto.setEMail(user.getEmail());
        userLoginDataResponseDto.setFirstName(user.getFirstName());
        userLoginDataResponseDto.setId(user.getId());
        userLoginDataResponseDto.setLastName(user.getLastName());
        userLoginDataResponseDto.setPhone(user.getPhone());
        userLoginDataResponseDto.setLastOnlineTime(user.getLastOnlineTime());
        MessagesPermission messagesPermission = user.isMessagePermissions()? MessagesPermission.ALL : MessagesPermission.FRIENDS;
        userLoginDataResponseDto.setMessagePermissions(messagesPermission);
        userLoginDataResponseDto.setToken(token);

        return userLoginDataResponseDto;
    }
}
