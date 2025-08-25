package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@Validated
@RequiredArgsConstructor
public class InternalUserAppSyncController {

    @Value("${internal.auth.token}")
    private String internalAuthToken;

    private final UserAppService userAppService;

    @PostMapping("/createandupdate")
    public ResponseEntity<UserAppDto> createAndUpdateUserApp(@RequestHeader(value = "X-Internal-Token", required = false) String token,
                                                             @Valid @RequestBody UserAppDto userAppDto) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!internalAuthToken.equals(token.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserAppDto createdOrUpdatedUserAppDto = userAppService.createAndUpdateUserApp(userAppDto);
        return ResponseEntity.ok(createdOrUpdatedUserAppDto);
    }
}
