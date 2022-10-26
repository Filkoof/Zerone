package ru.example.group.main.exception;

public class SendToKafkaException extends Exception {
    public SendToKafkaException(String message) {
        super(message);
    }
}
