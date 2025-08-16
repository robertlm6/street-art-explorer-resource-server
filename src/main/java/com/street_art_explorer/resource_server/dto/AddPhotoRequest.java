package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddPhotoRequest {
    @NotBlank
    private String publicId;

    @NotBlank
    private String url;

    @NotBlank
    private String secureUrl;

    private String format;
    private Integer width;
    private Integer height;
    private Integer bytes;
    private String assetId;
    private String thumbnailUrl;
    private Short position;
}
