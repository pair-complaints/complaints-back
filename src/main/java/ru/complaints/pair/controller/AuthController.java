package ru.complaints.pair.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.complaints.pair.dto.request.auth.RefreshJwtRequest;
import ru.complaints.pair.dto.request.auth.SignInRequest;
import ru.complaints.pair.dto.request.auth.SignUpRequest;
import ru.complaints.pair.dto.response.auth.JwtAuthenticationResponse;
import ru.complaints.pair.service.auth.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    /**
     * Метод для регистрации нового пользователя
     */
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    /**
     * Метод для авторизации существующего пользователя
     */
    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    /**
     * Метод для получения нового access токена по refresh токену
     */
    @Operation(summary = "Получение нового access токена по refresh токену")
    @PostMapping("/token")
    public JwtAuthenticationResponse getNewAccessToken(@RequestBody @Valid RefreshJwtRequest request) {
        return authenticationService.generateNewAccessToken(request);
    }
}