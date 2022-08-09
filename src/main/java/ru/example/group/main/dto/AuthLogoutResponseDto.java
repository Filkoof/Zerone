package ru.example.group.main.dto;

import java.time.LocalDateTime;

public class AuthLogoutResponseDto {

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
