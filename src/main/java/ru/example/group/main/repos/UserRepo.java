package ru.example.group.main.repos;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;
import ru.example.group.main.entity.User;

@ComponentScan
public interface UserRepo extends CrudRepository<User, Long> {
    User findByEmail(String username);

    Boolean existsByEmail(String user);
}
