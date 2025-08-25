package com.street_art_explorer.resource_server.mapper;

import com.street_art_explorer.resource_server.dto.MarkerRankingItem;
import com.street_art_explorer.resource_server.dto.UserRankingItem;
import com.street_art_explorer.resource_server.projection.MarkerRankingRow;
import com.street_art_explorer.resource_server.projection.UserRankingRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RankingMapper {
    @Mapping(target = "markersCreated", expression = "java(defaultInt(r.getMarkersCreated()))")
    @Mapping(target = "ratingsGiven", expression = "java(defaultInt(r.getRatingsGiven()))")
    @Mapping(target = "score", expression = "java(defaultDouble(r.getScore()))")
    UserRankingItem toUserItem(UserRankingRow r);

    List<UserRankingItem> toUserItems(List<UserRankingRow> rows);

    @Mapping(target = "ratingsCount", expression = "java(defaultInt(r.getRatingsCount()))")
    @Mapping(target = "wilson", expression = "java(defaultDouble(r.getWilson()))")
    MarkerRankingItem toMarkerItem(MarkerRankingRow r);

    List<MarkerRankingItem> toMarkerItems(List<MarkerRankingRow> rows);

    default int defaultInt(Integer v) {
        return v == null ? 0 : v;
    }

    default double defaultDouble(Double v) {
        return v == null ? 0.0 : v;
    }
}
