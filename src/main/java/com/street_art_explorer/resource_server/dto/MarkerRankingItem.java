package com.street_art_explorer.resource_server.dto;

public record MarkerRankingItem(
        Integer markerId,
        String title,
        Double lat,
        Double lng,
        Integer ratingsCount,
        Double wilson,
        Double avgNorm) {
}
