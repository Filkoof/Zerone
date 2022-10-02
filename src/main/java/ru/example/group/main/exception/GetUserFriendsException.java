package ru.example.group.main.exception;

import ru.example.group.main.dto.response.FriendsResponseDto;

public class GetUserFriendsException extends Exception {

    private final FriendsResponseDto friendsResponseDto;

    public GetUserFriendsException(String message, FriendsResponseDto friendsResponseDto) {
        super(message);
        this.friendsResponseDto = friendsResponseDto;
    }
}
