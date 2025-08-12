package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkerPhotoDto {
    private Integer id;
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private Integer width;
    private Integer height;
    private Integer bytes;
    private String thumbnailUrl;
    private Short position;
}
