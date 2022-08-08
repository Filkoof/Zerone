package ru.example.group.main.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.example.group.main.entity.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String eMail);

    Boolean existsByEmail(String eMail);

    UserEntity findByConfirmationCode(String code);
}
