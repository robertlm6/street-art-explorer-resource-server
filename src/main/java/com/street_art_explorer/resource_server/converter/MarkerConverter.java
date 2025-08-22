package com.street_art_explorer.resource_server.converter;

import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.MarkerPhotoDto;
import com.street_art_explorer.resource_server.dto.OwnerDto;
import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MarkerConverter {

    private final MarkerPhotoConverter markerPhotoConverter;

    public MarkerDto markerToMarkerDto(Marker marker, List<MarkerPhoto> photos) {
        if (marker == null) return null;

        List<MarkerPhotoDto> photoDtos = (photos == null ? Collections.<MarkerPhoto>emptyList() : photos)
                .stream()
                .filter(Objects::nonNull)
                .map(markerPhotoConverter::markerPhotoToMarkerPhotoDto)
                .toList();

        return MarkerDto.builder()
                .id(marker.getId())
                .authServerUserId(marker.getAuthServerUserId())
                .title(marker.getTitle())
                .description(marker.getDescription())
                .lat(marker.getLat())
                .lng(marker.getLng())
                .address(marker.getAddress())
                .avgRating(marker.getAvgRating())
                .ratingsCount(marker.getRatingsCount())
                .createdAt(marker.getCreatedAt())
                .updatedAt(marker.getUpdatedAt())
                .photos(photoDtos)
                .build();
    }

    public MarkerDto markerToMarkerDto(Marker marker, List<MarkerPhoto> photos, PublicUserDto owner) {
        OwnerDto ownerDto = null;
        if (owner != null) {
            ownerDto = OwnerDto.builder()
                    .id(owner.getId())
                    .username(owner.getUsername())
                    .avatarUrl(owner.getAvatarUrl())
                    .build();
        }

        String coverUrl = null;
        if (marker.getCoverPhotoId() != null && photos != null) {
            coverUrl = photos.stream()
                    .filter(p -> Objects.equals(p.getId(), marker.getCoverPhotoId()))
                    .map(MarkerPhoto::getUrl)
                    .findFirst().orElse(null);
        }

        List<MarkerPhotoDto> photoDtos = (photos == null ? Collections.<MarkerPhoto>emptyList() : photos)
                .stream()
                .filter(Objects::nonNull)
                .map(markerPhotoConverter::markerPhotoToMarkerPhotoDto)
                .toList();

        return MarkerDto.builder()
                .id(marker.getId())
                .authServerUserId(marker.getAuthServerUserId())
                .title(marker.getTitle())
                .description(marker.getDescription())
                .lat(marker.getLat())
                .lng(marker.getLng())
                .address(marker.getAddress())
                .avgRating(marker.getAvgRating())
                .ratingsCount(marker.getRatingsCount())
                .photos(photoDtos)
                .owner(ownerDto)
                .coverPhotoId(marker.getCoverPhotoId())
                .coverPhotoUrl(coverUrl)
                .build();
    }

    public MarkerDto markerToMarkerDtoBrief(Marker marker) {
        return MarkerDto.builder()
                .id(marker.getId())
                .authServerUserId(marker.getAuthServerUserId())
                .title(marker.getTitle())
                .description(marker.getDescription())
                .lat(marker.getLat())
                .lng(marker.getLng())
                .address(marker.getAddress())
                .avgRating(marker.getAvgRating())
                .ratingsCount(marker.getRatingsCount())
                .build();
    }

    public Marker markerDtoToMarker(MarkerDto markerDto) {
        return Marker.builder()
                .id(markerDto.getId())
                .authServerUserId(markerDto.getAuthServerUserId())
                .title(markerDto.getTitle())
                .description(markerDto.getDescription())
                .lat(markerDto.getLat())
                .lng(markerDto.getLng())
                .address(markerDto.getAddress())
                .avgRating(markerDto.getAvgRating())
                .ratingsCount(markerDto.getRatingsCount())
                .createdAt(markerDto.getCreatedAt())
                .updatedAt(markerDto.getUpdatedAt())
                .build();
    }

    public List<MarkerDto> markersToMarkerDtos(List<Marker> markers) {
        if (markers == null) return List.of();

        return markers.stream()
                .filter(Objects::nonNull)
                .map(m -> MarkerDto.builder()
                        .id(m.getId())
                        .authServerUserId(m.getAuthServerUserId())
                        .title(m.getTitle())
                        .description(m.getDescription())
                        .lat(m.getLat())
                        .lng(m.getLng())
                        .address(m.getAddress())
                        .avgRating(m.getAvgRating())
                        .ratingsCount(m.getRatingsCount())
                        .createdAt(m.getCreatedAt())
                        .updatedAt(m.getUpdatedAt())
                        .photos(List.of())
                        .build()
                )
                .toList();
    }
}
