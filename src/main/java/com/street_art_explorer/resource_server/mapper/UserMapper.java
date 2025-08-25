package com.street_art_explorer.resource_server.mapper;

import com.street_art_explorer.resource_server.dto.PublicUserDto;
import com.street_art_explorer.resource_server.dto.UserAppDto;
import com.street_art_explorer.resource_server.dto.UserAppPatchRequest;
import com.street_art_explorer.resource_server.dto.UserSummaryDto;
import com.street_art_explorer.resource_server.entity.UserApp;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    PublicUserDto toPublicUserDto(UserApp userApp);

    UserAppDto toUserAppDto(UserApp userApp);

    UserSummaryDto toUserSummaryDto(UserApp userApp);

    UserApp toUserApp(UserAppDto userAppDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void applyPatch(UserAppPatchRequest patch, @MappingTarget UserApp target);
}
