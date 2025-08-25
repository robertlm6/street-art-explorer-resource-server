package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.service.MarkerService;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserAppController {

    private final UserAppService userAppService;
    private final MarkerService markerService;

    @GetMapping("/{id}")
    public ResponseEntity<PublicUserDto> getPublicUserApp(@PathVariable @Positive Integer id) {
        PublicUserDto publicUserDto = userAppService.getPublicUserById(id);
        return ResponseEntity.ok(publicUserDto);
    }

    @GetMapping("/{id}/markers")
    public ResponseEntity<List<MarkerDto>> getUserMarkers(@PathVariable @Positive Integer id,
                                                          @RequestParam(defaultValue = "100") @Min(1) @Max(200) Integer limit) {
        List<MarkerDto> list = markerService.getUserMarkersBrief(id, limit);
        return ResponseEntity.ok(list);
    }
}
