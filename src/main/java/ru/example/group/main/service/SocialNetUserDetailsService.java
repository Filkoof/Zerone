package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controllers.AuthUserController;
import ru.example.group.main.controllers.GlobalExceptionHandlerController;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repositories.SocialNetUserRepository;
import ru.example.group.main.security.SocialNetUserDetails;

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
        UserEntity user = socialNetUserRepository.findUserEntityByEMail(s);
        if (user != null) {
            return new SocialNetUserDetails(user);
        }
        handlerController.handleUsernameNotFoundException(new UsernameNotFoundException("user not found doh!"));
        throw new UsernameNotFoundException("user not found doh!");
    }
}
