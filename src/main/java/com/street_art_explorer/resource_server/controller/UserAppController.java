package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.service.UserAppService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserAppController {

    private final UserAppService userAppService;

    public UserAppController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAppDto> createUserApp(@RequestBody UserAppDto userAppDto) {
        ResponseEntity<UserAppDto> response;

        try {
            UserAppDto createdUserAppDto = userAppService.createUserApp(userAppDto);
            response = new ResponseEntity<>(createdUserAppDto, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
