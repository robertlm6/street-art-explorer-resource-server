package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkerRankingItem {
    private Integer markerId;
    private String title;
    private Double lat;
    private Double lng;
    private Integer ratingsCount;
    private Double wilson;
    private Double avgNorm;
}
