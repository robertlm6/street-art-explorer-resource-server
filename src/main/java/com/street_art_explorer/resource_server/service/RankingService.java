package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.enums.Period;

public interface RankingService {
    FeedAndRankingResponse getUserRanking(Period period, int limit, int offset);

    FeedAndRankingResponse getMarkerRanking(Period period, int limit, int offset);
}
