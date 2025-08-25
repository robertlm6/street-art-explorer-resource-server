package com.street_art_explorer.resource_server.mapper;

import com.street_art_explorer.resource_server.dto.MarkerPhotoDto;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerPhotoMapper {
    MarkerPhotoDto toMarkerPhotoDto(MarkerPhoto markerPhoto);

    MarkerPhoto toMarkerPhoto(MarkerPhotoDto markerPhotoDto);
}
