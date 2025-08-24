package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.MarkerRankingResponse;
import com.street_art_explorer.resource_server.dto.Period;
import com.street_art_explorer.resource_server.dto.UserRankingResponse;

public interface RankingService {
    UserRankingResponse getUserRanking(Period period, int limit, int offset);

    MarkerRankingResponse getMarkerRanking(Period period, int limit, int offset);
}
