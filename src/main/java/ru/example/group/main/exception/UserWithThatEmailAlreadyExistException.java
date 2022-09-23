package ru.example.group.main.exception;

import ru.example.group.main.dto.response.ResultMessageDto;

public class UserWithThatEmailAlreadyExistException extends Exception{

    private final ResultMessageDto resultMessageDto;
    public UserWithThatEmailAlreadyExistException(String message, ResultMessageDto resultMessageDto) {
        super(message);
        this.resultMessageDto = resultMessageDto;
    }

}
