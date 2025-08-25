package com.street_art_explorer.resource_server.dto;

import java.math.BigDecimal;

public record RatingSummary(
        BigDecimal avgRating,
        Integer ratingsCount) {
}
