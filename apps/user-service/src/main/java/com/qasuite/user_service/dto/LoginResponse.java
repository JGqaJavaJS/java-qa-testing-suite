package com.qasuite.user_service.dto;

public record LoginResponse(String token, String tokenType, long expiresInMinutes) {
}