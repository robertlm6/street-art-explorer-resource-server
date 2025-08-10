package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.converter.UserAppConverter;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.UserAppService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
        if (userAppDto.getAuthServerUserId() == null || userAppDto.getUsername() == null || userAppDto.getEmail() == null) {
            throw new IllegalArgumentException("authServerUserId, username and email are required");
        }

        try {
            UserApp userApp = userAppRepository.findByAuthServerUserId(userAppDto.getAuthServerUserId())
                    .map(existing -> {
                        existing.setUsername(userAppDto.getUsername());
                        existing.setEmail(userAppDto.getEmail());
                        return existing;
                    }).orElseGet(() -> userAppConverter.userDtoToUserApp(userAppDto));

            UserApp savedUserApp = userAppRepository.save(userApp);

            return userAppConverter.userApptoToDto(savedUserApp);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User app already exists");
        } catch (Exception e) {
            throw new RuntimeException("Error creating user app");
        }
    }
}
