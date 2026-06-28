package ru.complaints.pair.dto.response.auth;

public record JwtAuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}