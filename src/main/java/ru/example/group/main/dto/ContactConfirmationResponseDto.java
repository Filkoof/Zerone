package ru.example.group.main.dto;

public class ContactConfirmationResponseDto {

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
