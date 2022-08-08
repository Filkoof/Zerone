package ru.example.group.main.data;

import ru.example.group.main.data.dto.LogoutDataDto;

import java.time.LocalDateTime;

public class AuthLogoutResponse {

    private LogoutDataDto data;
    private String error;
    private LocalDateTime timestamp;

    public LogoutDataDto getData() {
        return data;
    }

    public void setData(LogoutDataDto data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
