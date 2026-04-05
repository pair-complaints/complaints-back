package ru.complaints.pair.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.complaints.pair.config.security.CustomUserDetails;
import ru.complaints.pair.dao.RefreshSession;
import ru.complaints.pair.dao.repository.RefreshSessionRepository;
import ru.complaints.pair.exception.AuthException;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {
    @Value("${refresh.session.max-count}")
    private int maxSessionsCount;
    private final RefreshSessionRepository refreshSessionRepository;

    /**
     * Создание новой рефреш сессии.
     * Если по юзеру находится > maxSessionsCount сессий, то все уже существующие удаляются
     */
    @Transactional
    public void createNewRefreshSession(String fingerprint, String refreshToken, CustomUserDetails userDetails,
                                        Date expirationDate) {
        if (refreshSessionRepository.countAllByUserCredential_Username(userDetails.getUsername()) > maxSessionsCount) {
            refreshSessionRepository.deleteAllByUserCredential_Username(userDetails.getUsername());
        }
        RefreshSession refreshSession = new RefreshSession();
        refreshSession.setUserId(userDetails.getUserId());
        refreshSession.setFingerprint(fingerprint);
        refreshSession.setExpirationDate(expirationDate);
        String tokenHash = HashUtils.generateSHA256Hash(refreshToken);
        refreshSession.setRefreshTokenHash(tokenHash);
        refreshSessionRepository.save(refreshSession);
    }

    /**
     * Валидация рефреш сессии по refresh токену.
     * Достается по хэшу refresh токена запись с рефреш сессией и сразу же удаляется из БД
     * Сравнивается fingerprint полученной записи с переданным в метод, если совпадает, то сессия валидна
     */
    public void validateRefreshSession(String fingerprint, String refreshToken) {
        String tokenHash = HashUtils.generateSHA256Hash(refreshToken);
        Optional<RefreshSession> refreshSessionOptional = refreshSessionRepository.findByRefreshTokenHash(tokenHash);
        RefreshSession refreshSession = refreshSessionOptional.orElseThrow();
        refreshSessionRepository.delete(refreshSession);
        if (!fingerprint.equals(refreshSession.getFingerprint())) {
            throw new AuthException("Invalid refresh session for user: %s"
                    .formatted(refreshSession.getUserCredential().getUsername()));
        }
    }
}
