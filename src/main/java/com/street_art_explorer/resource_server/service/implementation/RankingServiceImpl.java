package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerRankingItem;
import com.street_art_explorer.resource_server.dto.UserRankingItem;
import com.street_art_explorer.resource_server.dto.enums.Period;
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

    @Override
    public FeedAndRankingResponse<UserRankingItem> getUserRanking(Period period, int limit, int offset) {
        LocalDateTime since = sinceTs(period);
        List<UserRankingRow> rows = rankingRepository.findUserRanking(since, limit, offset);

        var items = rows.stream()
                .map(r -> new UserRankingItem(
                        r.getUserId(),
                        r.getUsername(),
                        r.getAvatarUrl(),
                        r.getAuthServerUserId(),
                        r.getMarkersCreated() == null ? 0 : r.getMarkersCreated(),
                        r.getRatingsGiven() == null ? 0 : r.getRatingsGiven(),
                        r.getScore() == null ? 0.0 : r.getScore()
                ))
                .toList();

        Integer nextOffset = items.size() < limit ? null : (offset + limit);
        return new FeedAndRankingResponse<>(items, nextOffset);
    }

    @Override
    public FeedAndRankingResponse<MarkerRankingItem> getMarkerRanking(Period period, int limit, int offset) {
        LocalDateTime since = sinceTs(period);
        List<MarkerRankingRow> rows = rankingRepository.findMarkerRanking(since, limit, offset);

        var items = rows.stream().map(r ->
                new MarkerRankingItem(
                        r.getMarkerId(),
                        r.getTitle(),
                        r.getLat(),
                        r.getLng(),
                        r.getRatingsCount() == null ? 0 : r.getRatingsCount(),
                        r.getWilson() == null ? 0.0 : r.getWilson(),
                        r.getAvgNorm()
                )
        ).toList();

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
