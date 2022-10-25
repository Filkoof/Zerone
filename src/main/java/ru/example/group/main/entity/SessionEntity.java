package ru.example.group.main.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@Data
@RedisHash("Session")
public class SessionEntity implements Serializable {
    @Id
    private UUID session;
    @Indexed
    private Long userId;
}
