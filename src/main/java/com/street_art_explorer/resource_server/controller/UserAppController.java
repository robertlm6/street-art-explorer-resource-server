package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.service.UserAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAppController {

    private final UserAppService userAppService;

    @GetMapping("/{id}")
    public ResponseEntity<PublicUserDto> getPublicUserApp(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        PublicUserDto publicUserDto = userAppService.getPublicUserById(id);
        return ResponseEntity.ok(publicUserDto);
    }
}
