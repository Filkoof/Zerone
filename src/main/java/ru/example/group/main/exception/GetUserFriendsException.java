package ru.example.group.main.exception;

import lombok.Data;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;

@Data
public class GetUserFriendsException extends Exception{

    private FriendsResponseDto friendsResponseDto;

    public GetUserFriendsException(String message, FriendsResponseDto friendsResponseDto) {
        super(message);
        this.friendsResponseDto = friendsResponseDto;
    }
}
