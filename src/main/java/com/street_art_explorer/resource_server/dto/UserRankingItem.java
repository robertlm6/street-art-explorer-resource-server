package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserRankingItem {
    Integer userId;
    String username;
    String avatarUrl;
    Integer authServerUserId;
    long markersCreated;
    long ratingsGiven;
    double score;
}
