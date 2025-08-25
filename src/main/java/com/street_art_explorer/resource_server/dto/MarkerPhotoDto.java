package com.street_art_explorer.resource_server.dto;

public record MarkerPhotoDto(
        Integer id,
        String publicId,
        String url,
        String secureUrl,
        String format,
        Integer width,
        Integer height,
        Integer bytes,
        String thumbnailUrl,
        Short position) {
}
