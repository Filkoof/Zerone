package ru.example.group.main.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.example.group.main.data.dto.UserDto;

import java.time.LocalDateTime;

public class AuthLoginResponse {

    public UserDto data;

    public String error;

    @JsonProperty("timestamp")
    public LocalDateTime timeStamp;

    public UserDto getData() {
        return data;
    }

    public void setData(UserDto data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
