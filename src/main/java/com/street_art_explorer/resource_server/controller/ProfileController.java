package com.street_art_explorer.resource_server.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.service.UserAppService;

@RestController
public class ProfileController {

	private final UserAppService userAppService;

	public ProfileController(UserAppService userAppService) {
		this.userAppService = userAppService;
	}

	@GetMapping("/profile")
	public ResponseEntity<UserAppDto> getProfile(@AuthenticationPrincipal Jwt jwt) {
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Number authId = jwt.getClaim("id");
		if (authId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Integer userAuthId = authId.intValue();

		try {
			UserAppDto userAppDto = userAppService.getUserByAuthId(userAuthId);
			return ResponseEntity.ok(userAppDto);
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

	@PatchMapping("/profile")
	public ResponseEntity<UserAppDto> updateProfile(@AuthenticationPrincipal Jwt jwt,
			@RequestBody UserAppPatchRequest userAppPatchRequest) {
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Number authId = jwt.getClaim("id");
		if (authId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Integer userAuthId = authId.intValue();

		if (userAppPatchRequest == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		try {
			UserAppDto userAppDto = userAppService.patchUser(userAuthId, userAppPatchRequest);
			return ResponseEntity.ok(userAppDto);
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
