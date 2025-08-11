package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;

public interface UserAppService {

	UserAppDto createAndUpdateUserApp(UserAppDto userAppDto);

	UserAppDto getUserByAuthId(Integer id);

	UserAppDto patchUser(Integer id, UserAppPatchRequest userAppPatchRequest);

	PublicUserDto getPublicUserById(Integer id);
}
