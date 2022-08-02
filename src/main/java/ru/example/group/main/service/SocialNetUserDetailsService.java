package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.controllers.GlobalExceptionHandlerController;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repositories.SocialNetUserRepository;
import ru.example.group.main.security.SocialNetUserDetails;

@Service
public class SocialNetUserDetailsService implements UserDetailsService {

    private final SocialNetUserRepository socialNetUserRepository;
    @Autowired
    private GlobalExceptionHandlerController handlerController;


    @Autowired
    public SocialNetUserDetailsService(SocialNetUserRepository socialNetUserRepository) {
        this.socialNetUserRepository = socialNetUserRepository;
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
}
