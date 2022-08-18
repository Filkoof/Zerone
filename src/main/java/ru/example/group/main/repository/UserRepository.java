package ru.example.group.main.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

  UserEntity findByEmail(String eMail);

  Boolean existsByEmail(String eMail);

  UserEntity findByConfirmationCode(String code);
}
