package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateMarkerRequest(
        @Size(max = 120)
        String title,

        @Size(max = 4000)
        String description,

        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double lat,

        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double lng,

        @Size(max = 255)
        String address) {
}
