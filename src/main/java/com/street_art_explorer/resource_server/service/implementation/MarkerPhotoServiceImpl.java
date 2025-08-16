package com.street_art_explorer.resource_server.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.street_art_explorer.resource_server.converter.MarkerPhotoConverter;
import com.street_art_explorer.resource_server.dto.AddPhotoRequest;
import com.street_art_explorer.resource_server.dto.MarkerPhotoDto;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import com.street_art_explorer.resource_server.repository.MarkerPhotoRepository;
import com.street_art_explorer.resource_server.service.MarkerPhotoService;
import com.street_art_explorer.resource_server.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MarkerPhotoServiceImpl implements MarkerPhotoService {

    private final MarkerPhotoRepository markerPhotoRepository;

    private final MarkerPhotoConverter markerPhotoConverter;

    private final MarkerService markerService;

    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public MarkerPhotoDto createMarkerPhoto(Integer authId, Integer markerId, AddPhotoRequest addPhotoRequest) {
        Marker marker = markerService.requireOwned(markerId, authId);

        MarkerPhoto markerPhoto = MarkerPhoto.builder()
                .marker(marker)
                .publicId(addPhotoRequest.getPublicId())
                .url(addPhotoRequest.getUrl())
                .secureUrl(addPhotoRequest.getSecureUrl())
                .format(addPhotoRequest.getFormat())
                .width(addPhotoRequest.getWidth())
                .height(addPhotoRequest.getHeight())
                .bytes(addPhotoRequest.getBytes())
                .thumbnailUrl(addPhotoRequest.getThumbnailUrl())
                .position(addPhotoRequest.getPosition())
                .build();

        markerPhotoRepository.save(markerPhoto);
        return markerPhotoConverter.markerPhotoToMarkerPhotoDto(markerPhoto);
    }

    @Override
    @Transactional
    public void deleteMarkerPhoto(Integer authId, Integer markerId, Integer photoId) {
        markerService.requireOwned(markerId, authId);

        MarkerPhoto markerPhoto = markerPhotoRepository.findByIdAndMarkerId(photoId, markerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));

        try {
            var res = cloudinary.uploader().destroy(markerPhoto.getPublicId(), ObjectUtils.emptyMap());
            Object result = res.get("result");
            if (result == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error deleting in Cloudinary");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error deleting in Cloudinary");
        }

        markerPhotoRepository.delete(markerPhoto);
    }
}
