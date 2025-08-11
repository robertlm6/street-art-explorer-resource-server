package com.street_art_explorer.resource_server.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.service.UserAppService;

@RestController
@RequestMapping("/users")
public class UserAppController {

	private final UserAppService userAppService;

	public UserAppController(UserAppService userAppService) {
		this.userAppService = userAppService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<PublicUserDto> getPublicUserApp(@PathVariable Integer id) {
		if (id == null || id <= 0) {
			return ResponseEntity.badRequest().build();
		}

		try {
			PublicUserDto publicUserDto = userAppService.getPublicUserById(id);
			return ResponseEntity.ok(publicUserDto);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (DataAccessException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
