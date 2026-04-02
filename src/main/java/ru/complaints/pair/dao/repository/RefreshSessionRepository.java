package ru.complaints.pair.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.complaints.pair.dao.RefreshSession;

import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {

    void deleteAllByUserCredential_Username(String username);

    Optional<RefreshSession> findByRefreshTokenHash(String tokenHash);

    int countAllByUserCredential_Username(String username);
}
