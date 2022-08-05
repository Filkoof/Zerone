package ru.example.group.main.data;

import ru.example.group.main.data.dto.UserDto;

public class ContactConfirmationResponse {

    private String result;

    private UserDto userDto;

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
