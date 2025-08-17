package com.street_art_explorer.resource_server.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.street_art_explorer.resource_server.converter.UserAppConverter;
import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

    private final UserAppRepository userAppRepository;

    private final UserAppConverter userAppConverter;

    private final Cloudinary cloudinary;

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
    @Transactional(readOnly = true)
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
        if (isEmpty(userAppPatchRequest)) {
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

        if (userAppPatchRequest.getBio() != null) {
            userApp.setBio(userAppPatchRequest.getBio().trim());
        }

        String newUrl = userAppPatchRequest.getAvatarUrl();
        String newPid = userAppPatchRequest.getAvatarPublicId();

        if (newUrl == null && newPid == null) {
            userApp = userAppRepository.save(userApp);
            return userAppConverter.userAppToDto(userApp);
        }

        String oldPid = userApp.getAvatarPublicId();

        if (newUrl != null && newUrl.isBlank()) {
            if (oldPid != null && !oldPid.isBlank()) {
                destroyInCloudinaryOrFail(oldPid);
            }
            userApp.setAvatarUrl(null);
            userApp.setAvatarPublicId(null);

            userApp = userAppRepository.save(userApp);
            return userAppConverter.userAppToDto(userApp);
        }

        boolean isChanging =
                (newPid != null && !newPid.equals(oldPid)) ||
                        (newUrl != null && !newUrl.equals(userApp.getAvatarUrl()));

        if (isChanging) {
            if (oldPid != null && !oldPid.isBlank()) {
                destroyInCloudinaryOrFail(oldPid);
            }
            if (newUrl != null) userApp.setAvatarUrl(newUrl);
            if (newPid != null) userApp.setAvatarPublicId(newPid);
        }

        userApp = userAppRepository.save(userApp);
        return userAppConverter.userAppToDto(userApp);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicUserDto getPublicUserById(final Integer id) {

        UserApp userApp = userAppRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserApp not found for id: " + id));

        return userAppConverter.userAppToPublicUserDto(userApp);
    }

    private void destroyInCloudinaryOrFail(String publicId) {
        try {
            Map<?, ?> res = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            Object result = res.get("result");
            if (result == null) {
                throw new IllegalStateException("Cloudinary response without 'result'");
            }
            String r = String.valueOf(result);
            if (!"ok".equalsIgnoreCase(r) && !"not found".equalsIgnoreCase(r)) {
                throw new IllegalStateException("Cloudinary destroy failed: " + r);
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new IllegalStateException("Error contacting Cloudinary", e);
        }
    }

    private boolean isEmpty(UserAppPatchRequest req) {
        return req.getFirstName() == null
                && req.getLastName() == null
                && req.getBirthDate() == null
                && req.getBio() == null
                && req.getAvatarUrl() == null
                && req.getAvatarPublicId() == null;
    }
}
