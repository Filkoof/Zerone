package ru.example.group.main.exception;

public class EmailNotSentException extends Exception {
    public EmailNotSentException(String message) {
        super(message);
    }
}
