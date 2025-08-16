package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.UpdateMarkerRequest;
import com.street_art_explorer.resource_server.entity.Marker;

import java.util.List;

public interface MarkerService {
    Marker requireOwned(Integer markerId, Integer authorId);

    MarkerDto createMarker(Integer authId, CreateMarkerRequest markerDto);

    MarkerDto updateMarker(Integer authId, Integer markerId, UpdateMarkerRequest updateMarkerRequest);

    MarkerDto getMarker(Integer markerId);

    List<MarkerDto> getBBoxMarkers(double minLat, double maxLat, double minLng, double maxLng, int limit);

    List<MarkerDto> getNearMarkers(double lat, double lng, int limit);

    void deleteMarker(Integer authId, Integer markerId);
}
