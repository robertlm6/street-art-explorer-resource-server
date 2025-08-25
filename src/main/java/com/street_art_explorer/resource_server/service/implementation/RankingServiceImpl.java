package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerRankingItem;
import com.street_art_explorer.resource_server.dto.UserRankingItem;
import com.street_art_explorer.resource_server.dto.enums.Period;
import com.street_art_explorer.resource_server.mapper.RankingMapper;
import com.street_art_explorer.resource_server.projection.MarkerRankingRow;
import com.street_art_explorer.resource_server.projection.UserRankingRow;
import com.street_art_explorer.resource_server.repository.RankingRepository;
import com.street_art_explorer.resource_server.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;

    private final RankingMapper rankingMapper;

    @Override
    public FeedAndRankingResponse<UserRankingItem> getUserRanking(Period period, int limit, int offset) {
        LocalDateTime since = sinceTs(period);
        List<UserRankingRow> rows = rankingRepository.findUserRanking(since, limit, offset);

        List<UserRankingItem> items = rankingMapper.toUserItems(rows);
        Integer nextOffset = items.size() < limit ? null : (offset + limit);
        return new FeedAndRankingResponse<>(items, nextOffset);
    }

    @Override
    public FeedAndRankingResponse<MarkerRankingItem> getMarkerRanking(Period period, int limit, int offset) {
        LocalDateTime since = sinceTs(period);
        List<MarkerRankingRow> rows = rankingRepository.findMarkerRanking(since, limit, offset);

        List<MarkerRankingItem> items = rankingMapper.toMarkerItems(rows);
        Integer nextOffset = items.size() < limit ? null : offset + limit;
        return new FeedAndRankingResponse<>(items, nextOffset);
    }

    private LocalDateTime sinceTs(Period period) {
        return switch (period) {
            case month -> LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case year -> LocalDate.now().withDayOfYear(1).atStartOfDay();
            case all -> null;
        };
    }
}
