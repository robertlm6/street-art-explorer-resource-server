package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.converter.MarkerConverter;
import com.street_art_explorer.resource_server.converter.UserAppConverter;
import com.street_art_explorer.resource_server.dto.*;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.repository.MarkerPhotoRepository;
import com.street_art_explorer.resource_server.repository.MarkerRatingRepository;
import com.street_art_explorer.resource_server.repository.MarkerRepository;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.MarkerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerPhotoRepository markerPhotoRepository;
    private final MarkerRatingRepository markerRatingRepository;

    private final JwtAuthService jwtAuthService;

    private final MarkerConverter markerConverter;
    private final UserAppRepository userAppRepository;
    private final UserAppConverter userAppConverter;

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
        Marker marker = requireOwned(markerId, authId);

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

        UserApp owner = userAppRepository.findByAuthServerUserId(marker.getAuthServerUserId()).orElse(null);
        PublicUserDto ownerDto = owner == null ? null : userAppConverter.userAppToPublicUserDto(owner);

        MarkerDto markerDto = markerConverter.markerToMarkerDto(marker, photos, ownerDto);
        markerDto.setOwnedByMe(Objects.equals(markerDto.getAuthServerUserId(), currentAuthId));
        return markerDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getBBoxMarkers(
            double minLat, double maxLat, double minLng, double maxLng, int limit) {
        int lim = Math.max(1, Math.min(limit, 200));
        List<Marker> markers = markerRepository.findBBoxMarkers(minLat, maxLat, minLng, maxLng, lim);
        return assembleBriefDtos(markers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getNearMarkers(double lat, double lng, int limit) {
        int lim = Math.max(1, Math.min(limit, 200));
        List<Marker> markers = markerRepository.findNearMarkers(lat, lng, lim);
        return markerConverter.markersToMarkerDtos(markers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getUserMarkersBrief(Integer userId, int limit) {
        int lim = Math.max(1, Math.min(limit, 200));

        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Marker> all = markerRepository.findByAuthServerUserIdOrderByCreatedAtDesc(user.getAuthServerUserId());
        List<Marker> slice = all.size() > lim ? all.subList(0, lim) : all;
        return assembleBriefDtos(slice);
    }

    @Override
    @Transactional
    public void deleteMarker(Integer authId, Integer markerId) {
        Marker marker = requireOwned(markerId, authId);
        markerRepository.deleteById(marker.getId());
    }

    @Override
    @Transactional
    public void setCover(Integer authId, Integer markerId, Integer photoId) {
        Marker marker = requireOwned(markerId, authId);
        MarkerPhoto photo = markerPhotoRepository.findByIdAndMarkerId(photoId, markerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));
        marker.setCoverPhotoId(photo.getId());
        markerRepository.save(marker);
    }

    private List<MarkerDto> assembleBriefDtos(List<Marker> markers) {
        if (markers == null || markers.isEmpty()) return List.of();

        Integer currentAuthId = jwtAuthService.getOptionalAuthId().orElse(null);

        Set<Integer> authIds = markers.stream()
                .map(Marker::getAuthServerUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var userApps = userAppRepository.findByAuthServerUserIdIn(authIds);

        Map<Integer, PublicUserDto> ownersByAuthId = new HashMap<>();
        for (var ua : userApps) {
            ownersByAuthId.put(ua.getAuthServerUserId(), userAppConverter.userAppToPublicUserDto(ua));
        }

        Set<Integer> markerIds = markers.stream().map(Marker::getId).collect(Collectors.toSet());
        List<MarkerPhoto> photos = markerPhotoRepository
                .findByMarkerIdInOrderByMarkerIdAscPositionAscIdAsc(markerIds);

        Map<Integer, List<MarkerPhoto>> photosByMarkerId = new HashMap<>();
        for (MarkerPhoto p : photos) {
            photosByMarkerId.computeIfAbsent(p.getMarker().getId(), k -> new ArrayList<>()).add(p);
        }

        Map<Integer, MarkerPhoto> coverByMarkerId = new HashMap<>();
        for (Marker m : markers) {
            List<MarkerPhoto> list = photosByMarkerId.getOrDefault(m.getId(), List.of());
            if (list.isEmpty()) continue;

            MarkerPhoto cover = null;
            Integer coverId = m.getCoverPhotoId();
            if (coverId != null) {
                cover = list.stream().filter(p -> coverId.equals(p.getId())).findFirst().orElse(null);
            }
            if (cover == null) cover = list.get(0);
            coverByMarkerId.put(m.getId(), cover);
        }

        List<MarkerDto> result = new ArrayList<>(markers.size());
        for (Marker m : markers) {
            PublicUserDto ownerPublic = ownersByAuthId.get(m.getAuthServerUserId());

            UserSummaryDto ownerDto = null;
            if (ownerPublic != null) {
                ownerDto = new UserSummaryDto(ownerPublic.getId(), ownerPublic.getUsername(), ownerPublic.getAvatarUrl());
            }

            MarkerPhoto cover = coverByMarkerId.get(m.getId());
            String coverUrl = null;
            Integer coverId = null;
            if (cover != null) {
                coverId = cover.getId();
                coverUrl = cover.getThumbnailUrl() != null ? cover.getThumbnailUrl()
                        : cover.getSecureUrl() != null ? cover.getSecureUrl()
                        : cover.getUrl();
            }

            MarkerDto dto = markerConverter.markerToMarkerDtoBrief(m);
            dto.setOwner(ownerDto);
            dto.setCoverPhotoId(coverId);
            dto.setCoverPhotoUrl(coverUrl);
            dto.setOwnedByMe(Objects.equals(m.getAuthServerUserId(), currentAuthId));
            dto.setPhotos(null);

            result.add(dto);
        }
        return result;
    }
}
