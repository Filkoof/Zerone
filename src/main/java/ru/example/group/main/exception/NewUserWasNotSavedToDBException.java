package ru.example.group.main.exception;

public class NewUserWasNotSavedToDBException extends Exception{
    public NewUserWasNotSavedToDBException(String message) {
        super(message);
    }
}
