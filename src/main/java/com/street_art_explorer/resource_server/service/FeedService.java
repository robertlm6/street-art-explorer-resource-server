package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.FeedResponse;
import com.street_art_explorer.resource_server.dto.FeedType;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;

public interface FeedService {
    FeedResponse<MarkerFeedItem> getMarkersFeed(FeedType type, Integer limit, Integer offset, Double lat, Double lng,
                                                Double radiusKm, Integer days, Integer minVotes);
}
