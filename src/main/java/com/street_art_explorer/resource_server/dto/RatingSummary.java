package com.street_art_explorer.resource_server.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingSummary {
    private BigDecimal avgRating;
    private Integer ratingsCount;
}
