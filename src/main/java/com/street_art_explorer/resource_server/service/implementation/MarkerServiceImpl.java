package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.UpdateMarkerRequest;
import com.street_art_explorer.resource_server.dto.UserSummaryDto;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.mapper.MarkerMapper;
import com.street_art_explorer.resource_server.mapper.MarkerPhotoMapper;
import com.street_art_explorer.resource_server.mapper.UserMapper;
import com.street_art_explorer.resource_server.repository.MarkerPhotoRepository;
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
    private final UserAppRepository userAppRepository;

    private final JwtAuthService jwtAuthService;

    private final MarkerMapper markerMapper;
    private final UserMapper userMapper;
    private final MarkerPhotoMapper markerPhotoMapper;

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
        Marker marker = new Marker();
        marker.setAuthServerUserId(authId);
        marker.setTitle(createMarkerRequest.title());
        marker.setDescription(createMarkerRequest.description());
        marker.setLat(createMarkerRequest.lat());
        marker.setLng(createMarkerRequest.lng());
        marker.setAddress(createMarkerRequest.address());

        markerRepository.save(marker);

        UserApp owner = userAppRepository.findById(authId).orElse(null);
        UserSummaryDto ownerSummary = owner != null ? userMapper.toUserSummaryDto(owner) : null;

        MarkerDto markerDto = markerMapper.toMarkerDto(marker, authId, null, ownerSummary);
        markerDto.setPhotos(Collections.emptyList());
        return markerDto;
    }

    @Override
    @Transactional
    public MarkerDto updateMarker(Integer authId, Integer markerId, UpdateMarkerRequest updateMarkerRequest) {
        Marker marker = requireOwned(markerId, authId);

        if (updateMarkerRequest.title() != null) {
            marker.setTitle(updateMarkerRequest.title());
        }
        if (updateMarkerRequest.description() != null) {
            marker.setDescription(updateMarkerRequest.description());
        }
        if (updateMarkerRequest.lat() != null) {
            marker.setLat(updateMarkerRequest.lat());
        }
        if (updateMarkerRequest.lng() != null) {
            marker.setLng(updateMarkerRequest.lng());
        }
        if (updateMarkerRequest.address() != null) {
            marker.setAddress(updateMarkerRequest.address());
        }

        markerRepository.save(marker);

        List<MarkerPhoto> photos = markerPhotoRepository.findByMarkerIdOrderByIdAsc(markerId);
        MarkerPhoto cover = pickCoverPhoto(marker, photos);

        UserApp owner = userAppRepository.findByAuthServerUserId(marker.getAuthServerUserId()).orElse(null);
        UserSummaryDto ownerSummary = owner != null ? userMapper.toUserSummaryDto(owner) : null;

        MarkerDto markerDto = markerMapper.toMarkerDto(marker, authId, cover, ownerSummary);
        markerDto.setPhotos(photos.stream().map(markerPhotoMapper::toMarkerPhotoDto).toList());
        return markerDto;
    }

    @Override
    @Transactional(readOnly = true)
    public MarkerDto getMarker(Integer markerId) {
        Marker marker = markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));

        List<MarkerPhoto> photos = markerPhotoRepository.findByMarkerIdOrderByIdAsc(markerId);
        MarkerPhoto cover = pickCoverPhoto(marker, photos);

        Integer currentAuthId = jwtAuthService.getOptionalAuthId().orElse(null);

        UserApp owner = userAppRepository.findByAuthServerUserId(marker.getAuthServerUserId()).orElse(null);
        UserSummaryDto ownerSummary = owner != null ? userMapper.toUserSummaryDto(owner) : null;

        MarkerDto markerDto = markerMapper.toMarkerDto(marker, currentAuthId, cover, ownerSummary);
        markerDto.setPhotos(photos.stream().map(markerPhotoMapper::toMarkerPhotoDto).toList());
        return markerDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getBBoxMarkers(
            double minLat, double maxLat, double minLng, double maxLng, int limit) {
        List<Marker> markers = markerRepository.findBBoxMarkers(minLat, maxLat, minLng, maxLng, limit);
        return assembleBriefDtos(markers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkerDto> getUserMarkersBrief(Integer userId, int limit) {
        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Marker> all = markerRepository.findByAuthServerUserIdOrderByCreatedAtDesc(user.getAuthServerUserId());
        List<Marker> slice = all.size() > limit ? all.subList(0, limit) : all;
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

        List<UserApp> userApps = userAppRepository.findByAuthServerUserIdIn(authIds);

        Map<Integer, UserSummaryDto> ownersByAuthId = new HashMap<>();
        for (UserApp userApp : userApps) {
            ownersByAuthId.put(userApp.getAuthServerUserId(), userMapper.toUserSummaryDto(userApp));
        }

        Set<Integer> markerIds = markers.stream().map(Marker::getId).collect(Collectors.toSet());
        List<MarkerPhoto> photos = markerPhotoRepository
                .findByMarkerIdInOrderByMarkerIdAscPositionAscIdAsc(markerIds);

        Map<Integer, List<MarkerPhoto>> photosByMarkerId = new HashMap<>();
        for (MarkerPhoto markerPhoto : photos) {
            photosByMarkerId.computeIfAbsent(markerPhoto.getMarker().getId(), k -> new ArrayList<>()).add(markerPhoto);
        }

        Map<Integer, MarkerPhoto> coverByMarkerId = new HashMap<>();
        for (Marker marker : markers) {
            coverByMarkerId.put(marker.getId(), pickCoverPhoto(marker, photosByMarkerId.get(marker.getId())));
        }

        List<MarkerDto> result = new ArrayList<>(markers.size());
        for (Marker marker : markers) {
            UserSummaryDto ownerSummary = ownersByAuthId.get(marker.getAuthServerUserId());
            MarkerPhoto cover = coverByMarkerId.get(marker.getId());

            MarkerDto markerDto = markerMapper.toMarkerDto(marker, currentAuthId, cover, ownerSummary);
            markerDto.setPhotos(null);
            result.add(markerDto);
        }
        return result;
    }

    private MarkerPhoto pickCoverPhoto(Marker marker, List<MarkerPhoto> photos) {
        if (photos == null || photos.isEmpty()) return null;
        Integer coverId = marker.getCoverPhotoId();
        if (coverId != null) {
            for (MarkerPhoto p : photos) {
                if (coverId.equals(p.getId())) return p;
            }
        }
        return photos.get(0);
    }
}
