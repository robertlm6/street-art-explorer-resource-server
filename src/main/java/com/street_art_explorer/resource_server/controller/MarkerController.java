package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.*;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.MarkerPhotoService;
import com.street_art_explorer.resource_server.service.MarkerRatingService;
import com.street_art_explorer.resource_server.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;
    private final MarkerPhotoService markerPhotoService;
    private final MarkerRatingService markerRatingService;
    private final JwtAuthService jwtAuthService;

    @PostMapping("/create")
    public ResponseEntity<MarkerDto> createMarker(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody CreateMarkerRequest createMarkerRequest) {

        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        MarkerDto markerDto = markerService.createMarker(userAuthId, createMarkerRequest);
        return ResponseEntity.ok(markerDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MarkerDto> updateMarker(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable Integer id,
                                                  @RequestBody UpdateMarkerRequest updateMarkerRequest) {

        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        MarkerDto markerDto = markerService.updateMarker(userAuthId, id, updateMarkerRequest);
        return ResponseEntity.ok(markerDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerDto> getMarker(@PathVariable Integer id) {

        MarkerDto markerDto = markerService.getMarker(id);
        return ResponseEntity.ok(markerDto);
    }

    @GetMapping
    public ResponseEntity<List<MarkerDto>> getBBoxMarkers(@RequestParam double minLat,
                                                          @RequestParam double maxLat,
                                                          @RequestParam double minLng,
                                                          @RequestParam double maxLng,
                                                          @RequestParam int limit) {

        return ResponseEntity.ok(markerService.getBBoxMarkers(minLat, maxLat, minLng, maxLng, limit));
    }

    @GetMapping("/near")
    public ResponseEntity<List<MarkerDto>> getNearMarkers(@RequestParam double lat,
                                                          @RequestParam double lng,
                                                          @RequestParam(defaultValue = "100") int limit) {

        return ResponseEntity.ok(markerService.getNearMarkers(lat, lng, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MarkerDto> deleteMarker(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable Integer id) {

        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        markerService.deleteMarker(userAuthId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<MarkerPhotoDto> addPhoto(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Integer id,
                                                   @RequestBody @Valid AddPhotoRequest req) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        MarkerPhotoDto markerPhotoDto = markerPhotoService.createMarkerPhoto(authId, id, req);
        return ResponseEntity.ok(markerPhotoDto);
    }

    @DeleteMapping("/{markerId}/photos/{photoId}")
    public ResponseEntity<MarkerPhotoDto> deletePhoto(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable Integer markerId,
                                                      @PathVariable Integer photoId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        markerPhotoService.deleteMarkerPhoto(authId, markerId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{markerId}/ratings")
    public ResponseEntity<RatingSummary> createOrUpdateRating(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Integer markerId,
                                                              @Valid @RequestBody RateRequest rateRequest) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        RatingSummary ratingSummary = markerRatingService.createRating(authId, markerId, rateRequest.getScore());
        return ResponseEntity.ok(ratingSummary);
    }

    @GetMapping("/{markerId}/ratings/me")
    public ResponseEntity<RateResponse> getMyRating(@AuthenticationPrincipal Jwt jwt,
                                                    @PathVariable Integer markerId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        Optional<Short> score = markerRatingService.getRating(authId, markerId);
        return score.map(aShort -> ResponseEntity.ok(new RateResponse(aShort))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{markerId}/ratings/summary")
    public ResponseEntity<RatingSummary> getRatingSummary(@PathVariable Integer markerId) {
        RatingSummary ratingSummary = markerRatingService.getRatingSummary(markerId);
        return ResponseEntity.ok(ratingSummary);
    }

    @DeleteMapping("/{markerId}/ratings/me")
    public ResponseEntity<RatingSummary> deleteMyRating(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable Integer markerId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        RatingSummary ratingSummary = markerRatingService.deleteRating(authId, markerId);
        return ResponseEntity.ok(ratingSummary);
    }
}
