package com.street_art_explorer.resource_server.converter;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.entity.UserApp;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UserAppConverter {

    public UserAppDto userAppToDto(UserApp userApp) {
        if (userApp == null) return null;

        return UserAppDto.builder()
                .id(userApp.getId())
                .authServerUserId(userApp.getAuthServerUserId())
                .username(userApp.getUsername())
                .email(userApp.getEmail())
                .firstName(userApp.getFirstName())
                .lastName(userApp.getLastName())
                .birthDate(userApp.getBirthDate())
                .bio(userApp.getBio())
                .avatarUrl(userApp.getAvatarUrl())
                .avatarPublicId(userApp.getAvatarPublicId())
                .createdAt(userApp.getCreatedAt())
                .updatedAt(userApp.getUpdatedAt())
                .build();
    }

    public UserApp userDtoToUserApp(UserAppDto userAppDto) {
        if (userAppDto == null) return null;

        return UserApp.builder()
                .id(userAppDto.getId())
                .authServerUserId(userAppDto.getAuthServerUserId())
                .username(userAppDto.getUsername())
                .email(userAppDto.getEmail())
                .firstName(userAppDto.getFirstName())
                .lastName(userAppDto.getLastName())
                .birthDate(userAppDto.getBirthDate())
                .bio(userAppDto.getBio())
                .avatarUrl(userAppDto.getAvatarUrl())
                .avatarPublicId(userAppDto.getAvatarPublicId())
                .createdAt(userAppDto.getCreatedAt())
                .updatedAt(userAppDto.getUpdatedAt())
                .build();
    }

    public PublicUserDto userAppToPublicUserDto(UserApp userApp) {
        if (userApp == null) return null;

        return PublicUserDto.builder()
                .id(userApp.getId())
                .username(userApp.getUsername())
                .firstName(userApp.getFirstName())
                .lastName(userApp.getLastName())
                .birthDate(userApp.getBirthDate())
                .bio(userApp.getBio())
                .createdAt(userApp.getCreatedAt())
                .avatarUrl(userApp.getAvatarUrl())
                .avatarPublicId(userApp.getAvatarPublicId())
                .build();
    }
}
