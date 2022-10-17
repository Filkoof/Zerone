package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "blacklisted_tokens")
public class JwtBlacklistEntity implements Serializable {

    @Id
    @Column(name = "jwt_blacklisted")
    private String jwtBlacklistedToken;
    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;
}
