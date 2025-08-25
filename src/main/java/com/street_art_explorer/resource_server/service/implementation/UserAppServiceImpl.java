package com.street_art_explorer.resource_server.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.mapper.UserMapper;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

    private final UserAppRepository userAppRepository;

    private final UserMapper userMapper;

    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public UserAppDto createAndUpdateUserApp(UserAppDto userAppDto) {
        UserApp userApp =
                userAppRepository.findByAuthServerUserId(userAppDto.getAuthServerUserId())
                        .map(existing -> {
                            existing.setUsername(userAppDto.getUsername());
                            existing.setEmail(userAppDto.getEmail());
                            return existing;
                        })
                        .orElseGet(() -> userMapper.toUserApp(userAppDto));

        UserApp savedUserApp = userAppRepository.save(userApp);
        return userMapper.toUserAppDto(savedUserApp);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAppDto getUserByAuthId(final Integer id) {
        UserApp userApp = userAppRepository.findByAuthServerUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("UserApp not found for authId " + id));
        return userMapper.toUserAppDto(userApp);
    }

    @Override
    @Transactional
    public UserAppDto patchUser(final Integer id, final UserAppPatchRequest userAppPatchRequest) {
        if (isEmpty(userAppPatchRequest)) {
            throw new IllegalArgumentException("There are no fields to update");
        }

        if (userAppPatchRequest.firstName() != null && userAppPatchRequest.firstName().isBlank()) {
            throw new IllegalArgumentException("firstName cannot be blank");
        }
        if (userAppPatchRequest.lastName() != null && userAppPatchRequest.lastName().isBlank()) {
            throw new IllegalArgumentException("lastName cannot be blank");
        }
        if (userAppPatchRequest.birthDate() != null && userAppPatchRequest.birthDate().after(new Date())) {
            throw new IllegalArgumentException("birthDate cannot be a future date");
        }

        UserApp userApp = userAppRepository.findByAuthServerUserId(id)
                .orElseThrow(() -> new NoSuchElementException("UserApp was not found for authId " + id));

        userMapper.applyPatch(userAppPatchRequest, userApp);

        String newUrl = userAppPatchRequest.avatarUrl();
        String newPid = userAppPatchRequest.avatarPublicId();
        String oldPid = userApp.getAvatarPublicId();

        if (newUrl == null && newPid == null) {
            UserApp saved = userAppRepository.save(userApp);
            return userMapper.toUserAppDto(saved);
        }

        if (newUrl != null && newUrl.isBlank()) {
            if (oldPid != null && !oldPid.isBlank()) {
                destroyInCloudinaryOrFail(oldPid);
            }
            userApp.setAvatarUrl(null);
            userApp.setAvatarPublicId(null);
            UserApp saved = userAppRepository.save(userApp);
            return userMapper.toUserAppDto(saved);
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

        UserApp saved = userAppRepository.save(userApp);
        return userMapper.toUserAppDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicUserDto getPublicUserById(final Integer id) {
        UserApp userApp = userAppRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserApp not found for id: " + id));
        return userMapper.toPublicUserDto(userApp);
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
        return req.firstName() == null
                && req.lastName() == null
                && req.birthDate() == null
                && req.bio() == null
                && req.avatarUrl() == null
                && req.avatarPublicId() == null;
    }
}
