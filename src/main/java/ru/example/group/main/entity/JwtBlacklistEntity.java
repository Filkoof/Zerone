package ru.example.group.main.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
public class JwtBlacklistEntity {

    @Id
    @Column(name = "jwt_blacklisted", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jwtBlacklistedToken;

    @Column(name = "revocation_date", columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime revocationDate;

    public String getJwtBlacklisted() {
        return jwtBlacklistedToken;
    }

    public void setJwtBlacklisted(String jwtBlacklistedToken) {
        this.jwtBlacklistedToken = jwtBlacklistedToken;
    }

    public LocalDateTime getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(LocalDateTime revocationDate) {
        this.revocationDate = revocationDate;
    }
}
