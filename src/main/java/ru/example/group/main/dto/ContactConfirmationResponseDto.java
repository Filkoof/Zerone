package ru.example.group.main.dto;

public class ContactConfirmationResponseDto {

    private String result;

    private UserLoginDataResponseDto userLoginDataResponseDto;

    public UserLoginDataResponseDto getUserDto() {
        return userLoginDataResponseDto;
    }

    public void setUserDto(UserLoginDataResponseDto userLoginDataResponseDto) {
        this.userLoginDataResponseDto = userLoginDataResponseDto;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
