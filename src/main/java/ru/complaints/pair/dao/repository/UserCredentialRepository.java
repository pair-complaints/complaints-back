package ru.complaints.pair.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.complaints.pair.dao.UserCredential;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    /**
     * Поиск пользователя по переданному username
     */
    Optional<UserCredential> findByUsername(String username);

    /**
     * Проверка, существует ли пользователь с переданным username
     */
    boolean existsByUsername(String username);
}
