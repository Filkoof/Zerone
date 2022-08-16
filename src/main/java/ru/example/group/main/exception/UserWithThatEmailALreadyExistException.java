package ru.example.group.main.exception;

import ru.example.group.main.dto.ApiResponseDto;

public class UserWithThatEmailALreadyExistException extends Exception{

    private ApiResponseDto apiResponseDto;
    public UserWithThatEmailALreadyExistException(String message, ApiResponseDto apiResponseDto) {
        super(message);
        this.apiResponseDto = apiResponseDto;
    }

    public ApiResponseDto getApiResponseDto() {
        return apiResponseDto;
    }

    public void setApiResponseDto(ApiResponseDto apiResponseDto) {
        this.apiResponseDto = apiResponseDto;
    }
}
