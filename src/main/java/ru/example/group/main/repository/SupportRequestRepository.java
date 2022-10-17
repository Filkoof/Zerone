package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.SupportRequestEntity;

public interface SupportRequestRepository extends JpaRepository<SupportRequestEntity, Long> {

}
