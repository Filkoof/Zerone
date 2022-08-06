package ru.example.group.main.entity.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDao {

    private String message;

    public ApiDao(String message) {
        this.message = message;
    }
}
