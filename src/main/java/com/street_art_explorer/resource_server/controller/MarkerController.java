package com.street_art_explorer.resource_server.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.street_art_explorer.resource_server.dto.CreateMarkerRequest;
import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.MarkerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;
    private final JwtAuthService jwtAuthService;

    @PostMapping("/create")
    public ResponseEntity<MarkerDto> createMarker(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateMarkerRequest createMarkerRequest) {

        try {
            Integer userAuthId = jwtAuthService.requireAuthId(jwt);
            MarkerDto markerDto = markerService.createMarker(userAuthId, createMarkerRequest);
            return ResponseEntity.ok(markerDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
