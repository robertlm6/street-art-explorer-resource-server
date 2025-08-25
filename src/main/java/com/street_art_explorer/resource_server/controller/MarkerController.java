package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.*;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.MarkerPhotoService;
import com.street_art_explorer.resource_server.service.MarkerRatingService;
import com.street_art_explorer.resource_server.service.MarkerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/markers")
@Validated
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
        return ResponseEntity.status(HttpStatus.CREATED).body(markerDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MarkerDto> updateMarker(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable @Positive Integer id,
                                                  @Valid @RequestBody UpdateMarkerRequest updateMarkerRequest) {

        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        MarkerDto markerDto = markerService.updateMarker(userAuthId, id, updateMarkerRequest);
        return ResponseEntity.ok(markerDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerDto> getMarker(@PathVariable @Positive Integer id) {

        MarkerDto markerDto = markerService.getMarker(id);
        return ResponseEntity.ok(markerDto);
    }

    @GetMapping
    public ResponseEntity<List<MarkerDto>> getBBoxMarkers(@RequestParam @DecimalMin(value = "-90.0") @DecimalMax("90.0") double minLat,
                                                          @RequestParam @DecimalMin(value = "-90.0") @DecimalMax("90.0") double maxLat,
                                                          @RequestParam @DecimalMin(value = "-180.0") @DecimalMax("180.0") double minLng,
                                                          @RequestParam @DecimalMin(value = "-180.0") @DecimalMax("180.0") double maxLng,
                                                          @RequestParam @Min(1) @Max(200) int limit) {

        if (minLat > maxLat || minLng > maxLng) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "min must be <= max for lat/lng");
        }
        return ResponseEntity.ok(markerService.getBBoxMarkers(minLat, maxLat, minLng, maxLng, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MarkerDto> deleteMarker(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable @Positive Integer id) {

        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        markerService.deleteMarker(userAuthId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cover/{photoId}")
    public ResponseEntity<Void> setCover(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable @Positive Integer id,
                                         @PathVariable @Positive Integer photoId) {
        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        markerService.setCover(userAuthId, id, photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<MarkerPhotoDto> addPhoto(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable @Positive Integer id,
                                                   @Valid @RequestBody AddPhotoRequest req) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        MarkerPhotoDto markerPhotoDto = markerPhotoService.createMarkerPhoto(authId, id, req);
        return ResponseEntity.ok(markerPhotoDto);
    }

    @DeleteMapping("/{markerId}/photos/{photoId}")
    public ResponseEntity<MarkerPhotoDto> deletePhoto(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable @Positive Integer markerId,
                                                      @PathVariable @Positive Integer photoId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        markerPhotoService.deleteMarkerPhoto(authId, markerId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{markerId}/ratings")
    public ResponseEntity<RatingSummary> createOrUpdateRating(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable @Positive Integer markerId,
                                                              @Valid @RequestBody RateRequest rateRequest) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        RatingSummary ratingSummary = markerRatingService.createRating(authId, markerId, rateRequest.score());
        return ResponseEntity.ok(ratingSummary);
    }

    @GetMapping("/{markerId}/ratings/me")
    public ResponseEntity<RateResponse> getMyRating(@AuthenticationPrincipal Jwt jwt,
                                                    @PathVariable @Positive Integer markerId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        Optional<Short> score = markerRatingService.getRating(authId, markerId);
        return score.map(s -> ResponseEntity.ok(new RateResponse(s)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{markerId}/ratings/summary")
    public ResponseEntity<RatingSummary> getRatingSummary(@PathVariable @Positive Integer markerId) {
        RatingSummary ratingSummary = markerRatingService.getRatingSummary(markerId);
        return ResponseEntity.ok(ratingSummary);
    }

    @DeleteMapping("/{markerId}/ratings/me")
    public ResponseEntity<RatingSummary> deleteMyRating(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable @Positive Integer markerId) {
        Integer authId = jwtAuthService.requireAuthId(jwt);
        RatingSummary ratingSummary = markerRatingService.deleteRating(authId, markerId);
        return ResponseEntity.ok(ratingSummary);
    }
}
