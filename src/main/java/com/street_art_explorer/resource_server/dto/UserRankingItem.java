package com.street_art_explorer.resource_server.dto;

public record UserRankingItem(
        Integer userId,
        String username,
        String avatarUrl,
        Integer authServerUserId,
        long markersCreated,
        long ratingsGiven,
        double score) {
}
