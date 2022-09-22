package ru.example.group.main.exception;

import ru.example.group.main.dto.response.ResultMessageDto;

public class UserWithThatEmailAlreadyExistException extends Exception{

    private ResultMessageDto resultMessageDto;
    public UserWithThatEmailAlreadyExistException(String message, ResultMessageDto resultMessageDto) {
        super(message);
        this.resultMessageDto = resultMessageDto;
    }

    public ResultMessageDto getResultMessageDto() {
        return resultMessageDto;
    }

    public void setResultMessageDto(ResultMessageDto apiResponseDto) {
        this.resultMessageDto = resultMessageDto;
    }
}
