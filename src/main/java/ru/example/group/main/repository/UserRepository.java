package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String eMail);
    Boolean existsByEmail(String eMail);
    UserEntity findByConfirmationCode(String code);

    List<UserEntity> findUserEntitiesByCity(String city, Pageable nextPage);

}
