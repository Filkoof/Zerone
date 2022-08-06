package ru.example.group.main.service;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.dao.UserDao;
import ru.example.group.main.repos.UserRepo;

import java.time.LocalDateTime;


@Service
public record UserService(UserRepo userRepo) {

    public UserEntity addUser(UserDao userDao) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserEntity user = new UserEntity();
        user.setStatus(true);
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setPassword(passwordEncoder.encode(userDao.getPasswd1()));
        user.setEmail(userDao.getEmail());
        user.setRegDate(LocalDateTime.now());
        return user;
    }
}