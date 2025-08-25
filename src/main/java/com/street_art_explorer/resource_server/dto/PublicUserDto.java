package com.street_art_explorer.resource_server.dto;

import java.time.LocalDateTime;
import java.util.Date;

public record PublicUserDto(
        Integer id,
        String username,
        String firstName,
        String lastName,
        Date birthDate,
        String bio,
        LocalDateTime createdAt,
        String avatarUrl,
        String avatarPublicId) {
}
