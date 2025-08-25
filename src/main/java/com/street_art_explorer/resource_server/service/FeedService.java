package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.dto.enums.FeedType;

public interface FeedService {
    FeedAndRankingResponse<MarkerFeedItem> getMarkersFeed(FeedType type, Integer limit, Integer offset, Double lat, Double lng,
                                                          Double radiusKm, Integer days, Integer minVotes);
}
