package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.service.UserAppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class InternalUserAppSyncController {

    @Value("${internal.auth.token}")
    private String internalAuthToken;

    private final UserAppService userAppService;

    public InternalUserAppSyncController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @PostMapping("/createandupdate")
    public ResponseEntity<UserAppDto> createAndUpdateUserApp(@RequestHeader(value = "X-Internal-Token", required = false) String token,
                                                             @RequestBody UserAppDto userAppDto) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!internalAuthToken.equals(token.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UserAppDto createdOrUpdatedUserAppDto = userAppService.createAndUpdateUserApp(userAppDto);
            return ResponseEntity.ok(createdOrUpdatedUserAppDto);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
