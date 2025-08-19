package com.street_art_explorer.resource_server.service.implementation;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.street_art_explorer.resource_server.converter.MarkerConverter;
import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.UpdateMarkerRequest;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import com.street_art_explorer.resource_server.repository.MarkerPhotoRepository;
import com.street_art_explorer.resource_server.repository.MarkerRatingRepository;
import com.street_art_explorer.resource_server.repository.MarkerRepository;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.MarkerService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerPhotoRepository markerPhotoRepository;
    private final MarkerRatingRepository markerRatingRepository;

    private final JwtAuthService jwtAuthService;

    private final MarkerConverter markerConverter;

    @Transactional
    public Marker requireOwned(Integer markerId, Integer authorId) {
        Marker marker = markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));

        if (!marker.getAuthServerUserId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return marker;
    }

    @Override
    @Transactional
    public MarkerDto createMarker(Integer authId, CreateMarkerRequest createMarkerRequest) {
        if (createMarkerRequest.getLat() == null || createMarkerRequest.getLng() == null ||
                createMarkerRequest.getLat() < -90 || createMarkerRequest.getLat() > 90 ||
                createMarkerRequest.getLng() < -180 || createMarkerRequest.getLng() > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

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

    @Override
    @Transactional
    public MarkerDto updateMarker(Integer authId, Integer markerId, UpdateMarkerRequest updateMarkerRequest) {
        Marker marker = requireOwned(authId, markerId);

        if (updateMarkerRequest.getTitle() != null) {
            marker.setTitle(updateMarkerRequest.getTitle());
        }
        if (updateMarkerRequest.getDescription() != null) {
            marker.setDescription(updateMarkerRequest.getDescription());
        }
        if (updateMarkerRequest.getLat() != null) {
            marker.setLat(updateMarkerRequest.getLat());
        }
        if (updateMarkerRequest.getLng() != null) {
            marker.setLng(updateMarkerRequest.getLng());
        }
        if (updateMarkerRequest.getAddress() != null) {
            marker.setAddress(updateMarkerRequest.getAddress());
        }

        markerRepository.save(marker);
        List<MarkerPhoto> photos = markerPhotoRepository.findByMarkerIdOrderByIdAsc(markerId);

        return markerConverter.markerToMarkerDto(marker, photos);
    }

    @Override
    @Transactional(readOnly = true)
    public MarkerDto getMarker(Integer markerId) {
        Marker marker = markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));

        List<MarkerPhoto> photos = markerPhotoRepository.findByMarkerIdOrderByIdAsc(markerId);

        Integer currentAuthId = jwtAuthService.getOptionalAuthId().orElse(null);

        MarkerDto markerDto = markerConverter.markerToMarkerDto(marker, photos);
        markerDto.setOwnedByMe(Objects.equals(markerDto.getAuthServerUserId(), currentAuthId));
        return markerDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getBBoxMarkers(double minLat, double maxLat, double minLng, double maxLng, int limit) {
        int lim = Math.max(1, Math.min(limit, 500));
        List<Marker> markers = markerRepository.findBBoxMarkers(minLat, maxLat, minLng, maxLng, lim);

        Integer currentAuthId = jwtAuthService.getOptionalAuthId().orElse(null);

        List<MarkerDto> markerDtos = markerConverter.markersToMarkerDtos(markers);
        markerDtos.forEach(dto -> dto.setOwnedByMe(Objects.equals(dto.getAuthServerUserId(), currentAuthId)));
        return markerDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getNearMarkers(double lat, double lng, int limit) {
        int lim = Math.max(1, Math.min(limit, 200));
        List<Marker> markers = markerRepository.findNearMarkers(lat, lng, lim);
        return markerConverter.markersToMarkerDtos(markers);
    }

    @Override
    @Transactional
    public void deleteMarker(Integer authId, Integer markerId) {
        Marker marker = requireOwned(authId, markerId);
        markerRepository.deleteById(marker.getId());
    }

}
