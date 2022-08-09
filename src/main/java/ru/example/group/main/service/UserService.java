package ru.example.group.main.service;

import liquibase.util.StringUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Value("${mail.host}")
    private String mailHost;

    private final UserRepository userRepository;

    private final MailSender mailSender;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean addUser(UserRegisterDto userRegisterDto) {
        UserEntity userFromDB = userRepository.findByEmail(userRegisterDto.getEmail());
        if(userFromDB != null) {
            return false;
        }
        UserEntity user = new UserEntity();
        user.setStatus(true);
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPasswd1()));
        user.setEmail(userRegisterDto.getEmail());
        user.setRegDate(LocalDateTime.now());
        user.setApproved(false);

        user.setConfirmationCode(UUID.randomUUID().toString());
        userRepository.save(user);

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
        UserEntity user = userRepository.findByConfirmationCode(code);
        if(user == null) {
            return false;
        }
        user.setConfirmationCode(null);
        user.setApproved(true);
        userRepository.save(user);
        return true;
    }
}