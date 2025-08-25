package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.service.JwtAuthService;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
public class ProfileController {

    private final UserAppService userAppService;
    private final JwtAuthService jwtAuthService;

    @GetMapping("/profile")
    public ResponseEntity<UserAppDto> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        UserAppDto userAppDto = userAppService.getUserByAuthId(userAuthId);
        return ResponseEntity.ok(userAppDto);
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserAppDto> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                    @Valid @RequestBody UserAppPatchRequest userAppPatchRequest) {
        Integer userAuthId = jwtAuthService.requireAuthId(jwt);
        UserAppDto userAppDto = userAppService.patchUser(userAuthId, userAppPatchRequest);
        return ResponseEntity.ok(userAppDto);
    }
}
