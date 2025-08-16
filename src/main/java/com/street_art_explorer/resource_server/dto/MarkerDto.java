package com.street_art_explorer.resource_server.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkerDto {
    private Integer id;
    private Integer authServerUserId;
    private String title;
    private String description;
    private Double lat;
    private Double lng;
    private String address;
    private BigDecimal avgRating;
    private Integer ratingsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MarkerPhotoDto> photos;
}
