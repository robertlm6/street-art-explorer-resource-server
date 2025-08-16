package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.AddPhotoRequest;
import com.street_art_explorer.resource_server.dto.MarkerPhotoDto;

public interface MarkerPhotoService {
    MarkerPhotoDto createMarkerPhoto(Integer authId, Integer markerId, AddPhotoRequest addPhotoRequest);

    void deleteMarkerPhoto(Integer authId, Integer markerId, Integer photoId);
}
