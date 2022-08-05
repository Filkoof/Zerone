package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "blacklisted_tokens")
public class JwtBlacklistEntity {

    @Id
    @Column(name = "jwt_blacklisted", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jwtBlacklistedToken;

    @Column(name = "revocation_date", columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime revocationDate;
}

