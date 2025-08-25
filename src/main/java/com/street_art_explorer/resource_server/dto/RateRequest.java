package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateRequest(
        @NotNull
        @Min(1)
        @Max(5)
        Short score) {
}
