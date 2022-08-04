package ru.example.group.main.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.group.main.entity.Role;
import ru.example.group.main.entity.User;
import ru.example.group.main.entity.dao.UserDao;
import ru.example.group.main.repos.UserRepo;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email);
    }

    public User makeUserDaoToUser(UserDao userDao) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = new User();
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setPassword(passwordEncoder.encode(userDao.getPasswd1()));
        user.setEmail(userDao.getEmail());
        return user;
    }
}