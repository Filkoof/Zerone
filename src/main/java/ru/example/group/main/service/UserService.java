package ru.example.group.main.service;

import liquibase.util.StringUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.RegistrationCompleteDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Value("${mail.hostBack}")
    private String mailHost;

    private final UserRepository userRepository;

    private final ZeroneMailSender zeroneMailSender;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ZeroneMailSender zeroneMailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.zeroneMailSender = zeroneMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean addUser(UserRegisterDto userRegisterDto) throws Exception {
        UserEntity userFromDB = userRepository.findByEmail(userRegisterDto.getEmail());
        if (userFromDB != null) {
            return false;
        }
        UserEntity user = new UserEntity();
        try{
            user.setStatus(true);
            user.setFirstName(userRegisterDto.getFirstName());
            user.setLastName(userRegisterDto.getLastName());
            user.setPassword(passwordEncoder.encode(userRegisterDto.getPasswd1()));
            user.setEmail(userRegisterDto.getEmail());
            user.setRegDate(LocalDateTime.now());
            user.setApproved(false);
            user.setConfirmationCode(UUID.randomUUID().toString().substring(0,24));
            user.setPhoto("preliminary photo");
            userRepository.save(user);
        } catch (Exception e){
            return false;
        }


        try {
            if (!StringUtil.isEmpty(user.getEmail())) {
                String message = String.format(
                    "Hello, %s! \n" +
                        "Welcome to Sweater. Please, visit next link: http://" + mailHost + "/api/v1/account/registration_complete/%s",
                    user.getFirstName(),
                    user.getConfirmationCode()
                );
                zeroneMailSender.send(user.getEmail(), "Activation Code from Zerone", message);
            }
        } catch (Exception e){

        }

        return true;
    }

    public RegistrationCompleteDto activateUser(String code) {
        UserEntity user = userRepository.findByConfirmationCode(code);
        RegistrationCompleteDto registrationCompleteDto = new RegistrationCompleteDto();
        if (user == null) {
            return registrationCompleteDto;
        }
        user.setConfirmationCode(null);
        user.setApproved(true);
        userRepository.save(user);
        registrationCompleteDto.setEMail(user.getEmail());
        registrationCompleteDto.setKey(code);
        return registrationCompleteDto;
    }

    public ApiResponseDto createUser(UserRegisterDto userRegisterDto) throws Exception {
        ApiResponseDto response = new ApiResponseDto();
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User with that email already exists");
            return response;
        }
        if (addUser(userRegisterDto)){
            response.setStatus(HttpStatus.OK);
            response.setMessage("User created");
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User creation mistake. Please contact support.");
        }
        return response;
    }
}