package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateMarkerRequest {
    @Size(max = 120)
    private String title;
    @Size(max = 4000)
    private String description;
    private Double lat;
    private Double lng;
    private String address;
}
