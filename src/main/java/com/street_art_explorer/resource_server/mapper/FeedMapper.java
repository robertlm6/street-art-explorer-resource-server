package com.street_art_explorer.resource_server.mapper;

import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.projection.MarkerFeedRow;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedMapper {
    MarkerFeedItem toMarkerFeedItem(MarkerFeedRow markerFeedRow);

    List<MarkerFeedItem> toMarkerFeedItems(List<MarkerFeedRow> markerFeedRows);
}
