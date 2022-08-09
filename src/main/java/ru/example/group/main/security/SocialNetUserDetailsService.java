package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controllers.GlobalExceptionHandlerController;
import ru.example.group.main.dto.UserDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.repositories.SocialNetUserRepository;

import java.util.logging.Logger;

@Service
public class SocialNetUserDetailsService implements UserDetailsService {

    private final SocialNetUserRepository socialNetUserRepository;
    private GlobalExceptionHandlerController handlerController;
    private final Logger logger = Logger.getLogger(SocialNetUserDetailsService.class.getName());


    @Autowired
    public SocialNetUserDetailsService(SocialNetUserRepository socialNetUserRepository, GlobalExceptionHandlerController handlerController) {
        this.socialNetUserRepository = socialNetUserRepository;
        this.handlerController = handlerController;
    }

    public void save(UserEntity user) {
        socialNetUserRepository.save(user);
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

    public UserDto setUserDtoFromAuth(UserEntity user, String token) {
        UserDto userDto = new UserDto();

        userDto.setAbout(user.getAbout());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setBlocked(user.isBlocked());
        userDto.setCity(user.getCity());
        userDto.setCountry(user.getCountry());
        userDto.setPassword(null);
        userDto.setRegDate(user.getRegDate());
        userDto.setDeleted(user.isDeleted());
        userDto.seteMail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setId(user.getId());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setLastOnlineTime(user.getLastOnlineTime());
        MessagesPermission messagesPermission = user.isMessagePermissions()? MessagesPermission.ALL : MessagesPermission.FRIENDS;
        userDto.setMessagePermissions(messagesPermission);
        userDto.setToken(token);

        return userDto;
    }
}
