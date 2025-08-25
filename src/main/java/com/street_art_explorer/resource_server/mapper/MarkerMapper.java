package com.street_art_explorer.resource_server.mapper;

import com.street_art_explorer.resource_server.dto.MarkerDto;
import com.street_art_explorer.resource_server.dto.UserSummaryDto;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import org.mapstruct.*;

import java.util.Objects;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MarkerPhotoMapper.class})
public interface MarkerMapper {
    @Mappings({
            @Mapping(target = "photos", ignore = true),
            @Mapping(target = "owner", ignore = true),
            @Mapping(target = "ownedByMe", ignore = true),
            @Mapping(target = "coverPhotoUrl", ignore = true)
    })
    MarkerDto toMarkerDto(Marker marker);

    @InheritConfiguration(name = "toMarkerDto")
    MarkerDto toMarkerDto(Marker marker,
                          @Context Integer currentAuthId,
                          @Context MarkerPhoto coverPhoto,
                          @Context UserSummaryDto ownerSummary);

    @AfterMapping
    default void after(@MappingTarget MarkerDto dto,
                       Marker marker,
                       @Context Integer currentAuthId,
                       @Context MarkerPhoto coverPhoto,
                       @Context UserSummaryDto ownerSummary) {

        dto.setOwnedByMe(Objects.equals(marker.getAuthServerUserId(), currentAuthId));

        if (ownerSummary != null) {
            dto.setOwner(ownerSummary);
        }

        Integer coverId = marker.getCoverPhotoId();
        if (coverId == null && coverPhoto != null) {
            coverId = coverPhoto.getId();
        }
        dto.setCoverPhotoId(coverId);

        if (coverPhoto != null) {
            String url =
                    coverPhoto.getThumbnailUrl() != null ? coverPhoto.getThumbnailUrl() :
                            coverPhoto.getSecureUrl() != null ? coverPhoto.getSecureUrl() :
                                    coverPhoto.getUrl();
            dto.setCoverPhotoUrl(url);
        }
    }
}
