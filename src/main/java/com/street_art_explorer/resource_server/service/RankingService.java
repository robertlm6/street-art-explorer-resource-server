package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerRankingItem;
import com.street_art_explorer.resource_server.dto.UserRankingItem;
import com.street_art_explorer.resource_server.dto.enums.Period;

public interface RankingService {
    FeedAndRankingResponse<UserRankingItem> getUserRanking(Period period, int limit, int offset);

    FeedAndRankingResponse<MarkerRankingItem> getMarkerRanking(Period period, int limit, int offset);
}
