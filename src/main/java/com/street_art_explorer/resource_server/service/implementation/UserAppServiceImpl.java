package com.street_art_explorer.resource_server.service.implementation;

import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.street_art_explorer.resource_server.converter.UserAppConverter;
import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.UserAppService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserAppServiceImpl implements UserAppService {

	private final UserAppRepository userAppRepository;
	private final UserAppConverter userAppConverter;

	public UserAppServiceImpl(UserAppRepository userAppRepository, UserAppConverter userAppConverter) {
		this.userAppRepository = userAppRepository;
		this.userAppConverter = userAppConverter;
	}

	@Override
	@Transactional
	public UserAppDto createAndUpdateUserApp(UserAppDto userAppDto) {
		if (userAppDto.getAuthServerUserId() == null || userAppDto.getUsername() == null
				|| userAppDto.getEmail() == null) {
			throw new IllegalArgumentException("authServerUserId, username and email are required");
		}

		try {
			UserApp userApp =
					userAppRepository.findByAuthServerUserId(userAppDto.getAuthServerUserId()).map(existing -> {
						existing.setUsername(userAppDto.getUsername());
						existing.setEmail(userAppDto.getEmail());
						return existing;
					}).orElseGet(() -> userAppConverter.userDtoToUserApp(userAppDto));

			UserApp savedUserApp = userAppRepository.save(userApp);

			return userAppConverter.userAppToDto(savedUserApp);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("User app already exists");
		} catch (Exception e) {
			throw new RuntimeException("Error creating user app");
		}
	}

	@Override
	@Transactional
	public UserAppDto getUserByAuthId(final Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("authId is required");
		}

		UserApp userApp = userAppRepository.findByAuthServerUserId(id)
				.orElseThrow(() -> new EntityNotFoundException("UserApp not found for authId " + id));

		return userAppConverter.userAppToDto(userApp);
	}

	@Override
	@Transactional
	public UserAppDto patchUser(final Integer id, final UserAppPatchRequest userAppPatchRequest) {
		if (userAppPatchRequest.getFirstName() == null && userAppPatchRequest.getLastName() == null
				&& userAppPatchRequest.getBirthDate() == null) {
			throw new IllegalArgumentException("There are no fields to update");
		}

		if (userAppPatchRequest.getFirstName() != null && userAppPatchRequest.getFirstName().isBlank()) {
			throw new IllegalArgumentException("firstName cannot be blank");
		}

		if (userAppPatchRequest.getLastName() != null && userAppPatchRequest.getLastName().isBlank()) {
			throw new IllegalArgumentException("lastName cannot be blank");
		}

		if (userAppPatchRequest.getBirthDate() != null) {
			Date today = new Date();
			if (userAppPatchRequest.getBirthDate().after(today)) {
				throw new IllegalArgumentException("birthDate cannot be a future date");
			}
		}

		UserApp userApp = userAppRepository.findByAuthServerUserId(id)
				.orElseThrow(() -> new NoSuchElementException("UserApp was not found for authId " + id));

		if (userAppPatchRequest.getFirstName() != null) {
			userApp.setFirstName(userAppPatchRequest.getFirstName().trim());
		}

		if (userAppPatchRequest.getLastName() != null) {
			userApp.setLastName(userAppPatchRequest.getLastName().trim());
		}

		if (userAppPatchRequest.getBirthDate() != null) {
			userApp.setBirthDate(userAppPatchRequest.getBirthDate());
		}

		userApp = userAppRepository.save(userApp);

		return userAppConverter.userAppToDto(userApp);
	}

	@Override
	@Transactional
	public PublicUserDto getPublicUserById(final Integer id) {

		UserApp userApp = userAppRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("UserApp not found for id: " + id));
		
		return userAppConverter.userAppToPublicUserDto(userApp);
	}
}
