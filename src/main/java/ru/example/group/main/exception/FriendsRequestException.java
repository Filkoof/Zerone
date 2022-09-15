package ru.example.group.main.exception;

import lombok.Data;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FriendsResponseDto;

@Data
public class FriendsRequestException extends Exception{

    private CommonResponseDto<?> commonResponseDto;

    public FriendsRequestException(String message, CommonResponseDto<?> commonResponseDto) {
        super(message);
        this.commonResponseDto = commonResponseDto;
    }
}
