package ru.complaints.pair.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username,

        @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password,

        @NotNull
        String fingerprint
) {
}