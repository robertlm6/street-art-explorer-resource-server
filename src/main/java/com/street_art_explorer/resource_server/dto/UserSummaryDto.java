package com.street_art_explorer.resource_server.dto;

public record UserSummaryDto(
        Integer id,
        String username,
        String avatarUrl) {
}
