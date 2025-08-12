package com.street_art_explorer.resource_server.converter;

import org.springframework.stereotype.Component;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.entity.UserApp;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class UserAppConverter {

    public UserAppDto userAppToDto(UserApp userApp) {
        return new UserAppDto(userApp.getAuthServerUserId(), userApp.getUsername(), userApp.getEmail(),
                userApp.getFirstName(), userApp.getLastName(), userApp.getBirthDate(), userApp.getCreatedAt(),
                userApp.getUpdatedAt());
    }

    public UserApp userDtoToUserApp(UserAppDto userAppDto) {
        UserApp user = new UserApp();

        user.setAuthServerUserId(userAppDto.getAuthServerUserId());
        user.setUsername(userAppDto.getUsername());
        user.setEmail(userAppDto.getEmail());
        user.setFirstName(userAppDto.getFirstName());
        user.setLastName(userAppDto.getLastName());
        user.setBirthDate(userAppDto.getBirthDate());
        user.setCreatedAt(userAppDto.getCreatedAt());
        user.setUpdatedAt(userAppDto.getUpdatedAt());

        return user;
    }

    public PublicUserDto userAppToPublicUserDto(UserApp userApp) {
        PublicUserDto publicUserDto = new PublicUserDto();

        publicUserDto.setId(userApp.getId());
        publicUserDto.setUsername(userApp.getUsername());
        publicUserDto.setFirstName(userApp.getFirstName());
        publicUserDto.setLastName(userApp.getLastName());
        publicUserDto.setBirthDate(userApp.getBirthDate());

        return publicUserDto;
    }
}
