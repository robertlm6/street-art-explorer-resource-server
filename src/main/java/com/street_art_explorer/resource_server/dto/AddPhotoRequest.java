package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddPhotoRequest {
    @NotBlank
    @Size(max = 255)
    private String publicId;

    @NotBlank
    @Size(max = 2048)
    @URL
    private String url;

    @NotBlank
    @Size(max = 2048)
    @URL
    private String secureUrl;

    @Size(max = 20)
    private String format;

    @PositiveOrZero
    private Integer width;

    @PositiveOrZero
    private Integer height;

    @PositiveOrZero
    private Integer bytes;

    @Size(max = 255)
    private String assetId;

    @Size(max = 2048)
    @URL
    private String thumbnailUrl;

    @PositiveOrZero
    private Short position;
}
