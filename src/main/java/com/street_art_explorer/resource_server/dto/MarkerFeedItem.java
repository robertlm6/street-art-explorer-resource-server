package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkerFeedItem {
    private Integer markerId;
    private String title;
    private Double lat;
    private Double lng;
    private String thumbnailUrl;

    private String address;
    private String description;
    private LocalDateTime createdAt;
    private Integer ratingsCount;
    private Double avgRating;

    private Integer creatorUserId;
    private String creatorUsername;
    private String creatorAvatarUrl;

    private Double distanceKm;
    private Integer recentVotes;
    private Double wilson;
}
