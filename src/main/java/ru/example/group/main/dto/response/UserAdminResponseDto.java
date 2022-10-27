package ru.example.group.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;

@Data
@AllArgsConstructor
public class UserAdminResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isDeleted;
    private boolean isBlocked;
    private String photo;
    private String userRole;

}
