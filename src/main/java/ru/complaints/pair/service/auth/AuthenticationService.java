package ru.complaints.pair.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.complaints.pair.config.security.CustomUserDetails;
import ru.complaints.pair.config.security.JwtProvider;
import ru.complaints.pair.dto.request.auth.RefreshJwtRequest;
import ru.complaints.pair.dto.request.auth.SignInRequest;
import ru.complaints.pair.dto.request.auth.SignUpRequest;
import ru.complaints.pair.dto.response.auth.JwtAuthenticationResponse;
import ru.complaints.pair.exception.AuthException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserCredentialService userCredentialService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshSessionService refreshSessionService;

    /**
     * Регистрация нового пользователя
     * Если пользователь с таким username уже существует, то выкенется исключение
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String passwordHash = passwordEncoder.encode(request.password());
        CustomUserDetails user = userCredentialService.create(request.username(), passwordHash);
        JwtAuthenticationResponse response = buildNewJwtAuthenticationResponse(user);
        createNewRefreshSession(request.fingerprint(), user, response.refreshToken());
        return response;
    }

    /**
     * Авторизация существующего пользователя по username и password
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        CustomUserDetails user = userCredentialService.loadUserByUsername(request.username());
        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            JwtAuthenticationResponse response = buildNewJwtAuthenticationResponse(user);
            createNewRefreshSession(request.fingerprint(), user, response.refreshToken());
            return response;
        }
        throw new AuthException("Invalid username or password");
    }

    /**
     * Получение нового access токена по refresh токену
     * Refresh токен тоже обновляется
     */
    public JwtAuthenticationResponse generateNewAccessToken(RefreshJwtRequest request) {
        if (jwtProvider.validateRefreshToken(request.refreshToken())) {
            String username = jwtProvider.getRefreshClaims(request.refreshToken()).getSubject();
            CustomUserDetails user = userCredentialService.loadUserByUsername(username);
            refreshSessionService.validateRefreshSession(request.fingerprint(), request.refreshToken());
            JwtAuthenticationResponse response = buildNewJwtAuthenticationResponse(user);
            createNewRefreshSession(request.fingerprint(), user, response.refreshToken());
            return response;
        }
        throw new AuthException("Invalid refresh token");
    }

    private JwtAuthenticationResponse buildNewJwtAuthenticationResponse(CustomUserDetails user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    private void createNewRefreshSession(String fingerprint, CustomUserDetails user, String refreshToken) {
        Date expirationDate = jwtProvider.getRefreshClaims(refreshToken).getExpiration();
        refreshSessionService.createNewRefreshSession(fingerprint, refreshToken, user, expirationDate);
    }
}