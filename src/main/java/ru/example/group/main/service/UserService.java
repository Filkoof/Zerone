package ru.example.group.main.service;

import liquibase.util.StringUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.dao.UserDao;
import ru.example.group.main.repos.UserRepo;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Value("${mail.host}")
    private String mailHost;

    private final UserRepo userRepo;

    private final MailSender mailSender;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepo userRepo, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean addUser(UserDao userDao) {
        UserEntity userFromDB = userRepo.findByEmail(userDao.getEmail());
        if(userFromDB != null) {
            return false;
        }
        UserEntity user = new UserEntity();
        user.setStatus(true);
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setPassword(passwordEncoder.encode(userDao.getPasswd1()));
        user.setEmail(userDao.getEmail());
        user.setRegDate(LocalDateTime.now());
        user.setApproved(false);

        user.setConfirmationCode(UUID.randomUUID().toString());
        userRepo.save(user);

        if(!StringUtil.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link: " + mailHost + "/activate/%s",
                    user.getFirstName(),
                    user.getConfirmationCode()
            );
            mailSender.send(user.getEmail(), "Activation Code from Zerone", message);
        }

        return true;
    }

    public boolean activateUser(String code) {
        UserEntity user = userRepo.findByConfirmationCode(code);
        if(user == null) {
            return false;
        }
        user.setConfirmationCode(null);
        user.setApproved(true);
        userRepo.save(user);
        return true;
    }
}