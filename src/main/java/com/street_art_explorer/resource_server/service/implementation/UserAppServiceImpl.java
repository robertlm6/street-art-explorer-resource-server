package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.converter.UserAppConverter;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.repository.UserAppRepository;
import com.street_art_explorer.resource_server.service.UserAppService;
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
    public UserAppDto createUserApp(UserAppDto userAppDto) {
        try {
            UserApp userApp = userAppConverter.userDtoToUserApp(userAppDto);

            UserApp savedUserApp = userAppRepository.save(userApp);

            return userAppConverter.userApptoToDto(savedUserApp);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User app already exists");
        } catch (Exception e) {
            throw new RuntimeException("Error creating user app");
        }
    }
}
