package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.UserAppDto;

public interface UserAppService {

    UserAppDto createAndUpdateUserApp(UserAppDto userAppDto);
}
