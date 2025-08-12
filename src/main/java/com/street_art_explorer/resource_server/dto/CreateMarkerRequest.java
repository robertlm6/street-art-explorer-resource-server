package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateMarkerRequest {
    @NotBlank
    @Size(max = 120)
    private String title;
    @Size(max = 4000)
    private String description;
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;
    private String address;
}
