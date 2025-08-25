package com.street_art_explorer.resource_server.dto;

import java.time.LocalDateTime;

public record MarkerFeedItem(
        Integer markerId,
        String title,
        Double lat,
        Double lng,
        String thumbnailUrl,

        String address,
        String description,
        LocalDateTime createdAt,
        Integer ratingsCount,
        Double avgRating,

        Integer creatorUserId,
        String creatorUsername,
        String creatorAvatarUrl,

        Double distanceKm,
        Integer recentVotes,
        Double wilson) {
}
