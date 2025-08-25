package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.*;

public record CreateMarkerRequest(
        @NotBlank
        @Size(max = 120)
        String title,

        @Size(max = 4000)
        String description,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double lat,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double lng,

        @Size(max = 255)
        String address) {
}
