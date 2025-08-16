package com.street_art_explorer.resource_server.converter;

import org.springframework.stereotype.Component;

import com.street_art_explorer.resource_server.dto.MarkerPhotoDto;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class MarkerPhotoConverter {

    public MarkerPhotoDto markerPhotoToMarkerPhotoDto(MarkerPhoto markerPhoto) {
        if (markerPhoto == null) return null;

        return MarkerPhotoDto.builder()
                .id(markerPhoto.getId())
                .publicId(markerPhoto.getPublicId())
                .secureUrl(markerPhoto.getSecureUrl())
                .url(markerPhoto.getUrl())
                .format(markerPhoto.getFormat())
                .width(markerPhoto.getWidth())
                .height(markerPhoto.getHeight())
                .bytes(markerPhoto.getBytes())
                .build();
    }

    public MarkerPhoto markerPhotoDtoToMarkerPhoto(MarkerPhotoDto markerPhotoDto) {
        if (markerPhotoDto == null) return null;

        return MarkerPhoto.builder()
                .id(markerPhotoDto.getId())
                .publicId(markerPhotoDto.getPublicId())
                .url(markerPhotoDto.getUrl())
                .secureUrl(markerPhotoDto.getSecureUrl())
                .format(markerPhotoDto.getFormat())
                .width(markerPhotoDto.getWidth())
                .height(markerPhotoDto.getHeight())
                .bytes(markerPhotoDto.getBytes())
                .thumbnailUrl(markerPhotoDto.getThumbnailUrl())
                .position(markerPhotoDto.getPosition())
                .build();
    }
}
