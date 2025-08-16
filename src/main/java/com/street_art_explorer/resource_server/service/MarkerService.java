package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;

public interface MarkerService {
    public MarkerDto createMarker(Integer authId, CreateMarkerRequest markerDto);
}
