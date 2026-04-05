package ru.complaints.pair.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.complaints.pair.dao.RefreshSession;

import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {

    /**
     * Удаление всех сессий пользователя с переданным username
     */
    void deleteAllByUserCredential_Username(String username);

    /**
     * Поиск записи по refresh_token_hash = tokenHash
     */
    Optional<RefreshSession> findByRefreshTokenHash(String tokenHash);

    /**
     * Подсчет сессий пользователя с переданным username
     */
    int countAllByUserCredential_Username(String username);
}
