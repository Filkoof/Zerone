package ru.example.group.main.exception;

public class EmailOrPasswordChangeException extends Exception{
    public EmailOrPasswordChangeException(String message) {
        super(message);
    }
}
