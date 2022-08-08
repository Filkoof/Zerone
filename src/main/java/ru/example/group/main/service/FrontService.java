package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FrontService {

    @Value("${front.domen}")
    private String domen;

    public String getDomen(){
        return domen;
    }
}
