package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controller.GlobalExceptionHandlerController;
import ru.example.group.main.dto.UserDataResponseDto;
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

    public UserDataResponseDto setUserDataResponseDto(UserEntity user, String token) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        userDataResponseDto.setAbout(user.getAbout());
        userDataResponseDto.setBirthDate(user.getBirthDate());
        userDataResponseDto.setBlocked(user.isBlocked());
        userDataResponseDto.setCity(user.getCity());
        userDataResponseDto.setCountry(user.getCountry());
        userDataResponseDto.setPassword(null);
        userDataResponseDto.setRegDate(user.getRegDate());
        userDataResponseDto.setDeleted(user.isDeleted());
        userDataResponseDto.setEMail(user.getEmail());
        userDataResponseDto.setFirstName(user.getFirstName());
        userDataResponseDto.setId(user.getId());
        userDataResponseDto.setLastName(user.getLastName());
        userDataResponseDto.setPhone(user.getPhone());
        userDataResponseDto.setPhoto(user.getPhoto());
        userDataResponseDto.setLastOnlineTime(user.getLastOnlineTime());
        MessagesPermission messagesPermission = user.isMessagePermissions()? MessagesPermission.ALL : MessagesPermission.FRIENDS;
        userDataResponseDto.setMessagePermissions(messagesPermission);
      //  userDataResponseDto.setStatus(true);
        userDataResponseDto.setToken(token);
        return userDataResponseDto;
    }
}
