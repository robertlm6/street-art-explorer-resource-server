package com.street_art_explorer.resource_server.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.street_art_explorer.resource_server.converter.MarkerConverter;
import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.repository.MarkerPhotoRepository;
import com.street_art_explorer.resource_server.repository.MarkerRatingRepository;
import com.street_art_explorer.resource_server.repository.MarkerRepository;
import com.street_art_explorer.resource_server.service.MarkerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerPhotoRepository markerPhotoRepository;
    private final MarkerRatingRepository markerRatingRepository;
    private final MarkerConverter markerConverter;

    @Override
    @Transactional
    public MarkerDto createMarker(Integer authId, CreateMarkerRequest createMarkerRequest) {
        Marker marker = new Marker();
        marker.setAuthServerUserId(authId);
        marker.setTitle(createMarkerRequest.getTitle());
        marker.setDescription(createMarkerRequest.getDescription());
        marker.setLat(createMarkerRequest.getLat());
        marker.setLng(createMarkerRequest.getLng());
        marker.setAddress(createMarkerRequest.getAddress());
        markerRepository.save(marker);
        return markerConverter.markerToMarkerDto(marker, List.of());
    }
}
