package ru.example.group.main.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("Session")
public class SessionEntity implements Serializable {
    @Id
    private String session;
    private String userId;
}
