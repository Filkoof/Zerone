package ru.example.group.main.exception;

import lombok.Data;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;

@Data
public class FriendsRequestException extends Exception{

    private ResultMessageDto resultMessageDto;

    public FriendsRequestException(String message, ResultMessageDto resultMessageDto) {
        super(message);
        this.resultMessageDto = resultMessageDto;
    }
}
