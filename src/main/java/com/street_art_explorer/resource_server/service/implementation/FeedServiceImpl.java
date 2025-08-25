package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.dto.enums.FeedType;
import com.street_art_explorer.resource_server.mapper.FeedMapper;
import com.street_art_explorer.resource_server.projection.MarkerFeedRow;
import com.street_art_explorer.resource_server.repository.FeedRepository;
import com.street_art_explorer.resource_server.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;

    private final FeedMapper feedMapper;

    @Override
    public FeedAndRankingResponse<MarkerFeedItem> getMarkersFeed(FeedType type, Integer limit, Integer offset, Double lat, Double lng,
                                                                 Double radiusKm, Integer days, Integer minVotes) {
        List<MarkerFeedRow> rows;

        switch (type) {
            case nearby -> {
                double la = (lat == null) ? 40.4168 : lat; // fallback: Madrid centro
                double ln = (lng == null) ? -3.7038 : lng;
                double rk = (radiusKm == null) ? 10.0 : radiusKm;
                rows = feedRepository.findNearby(la, ln, rk, limit, offset);
            }
            case trending -> {
                int d = (days == null) ? 14 : days;
                rows = feedRepository.findTrending(d, limit, offset);
            }
            case top -> {
                int mv = (minVotes == null) ? 3 : minVotes;
                rows = feedRepository.findTop(mv, limit, offset);
            }
            case newest -> {
                rows = feedRepository.findNewest(limit, offset);
            }
            default -> {
                rows = feedRepository.findNewest(limit, offset);
            }
        }

        List<MarkerFeedItem> items = feedMapper.toMarkerFeedItems(rows);
        Integer nextOffset = items.size() < limit ? null : offset + limit;
        return new FeedAndRankingResponse<>(items, nextOffset);
    }
}
