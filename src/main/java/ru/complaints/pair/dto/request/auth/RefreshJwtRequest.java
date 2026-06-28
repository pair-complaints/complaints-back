package ru.complaints.pair.dto.request.auth;

import jakarta.validation.constraints.NotNull;

public record RefreshJwtRequest(
        @NotNull
        String refreshToken,
        @NotNull
        String fingerprint
) {
}