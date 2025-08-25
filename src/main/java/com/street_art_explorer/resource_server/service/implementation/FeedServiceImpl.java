package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.dto.enums.FeedType;
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

    @Override
    public FeedAndRankingResponse<MarkerFeedItem> getMarkersFeed(FeedType type, Integer limit, Integer offset, Double lat, Double lng,
                                                                 Double radiusKm, Integer days, Integer minVotes) {
        int lim = limit == null ? 20 : Math.max(1, Math.min(100, limit));
        int off = offset == null ? 0 : Math.max(0, offset);

        List<MarkerFeedRow> rows;

        switch (type) {
            case nearby -> {
                double la = (lat == null) ? 40.4168 : lat; // fallback: Madrid centro
                double ln = (lng == null) ? -3.7038 : lng;
                double rk = (radiusKm == null) ? 10.0 : Math.max(0.5, Math.min(100.0, radiusKm));
                rows = feedRepository.findNearby(la, ln, rk, lim, off);
            }
            case trending -> {
                int d = (days == null) ? 14 : Math.max(1, Math.min(90, days));
                rows = feedRepository.findTrending(d, lim, off);
            }
            case top -> {
                int mv = (minVotes == null) ? 3 : Math.max(1, Math.min(50, minVotes));
                rows = feedRepository.findTop(mv, lim, off);
            }
            case newest -> {
                rows = feedRepository.findNewest(lim, off);
            }
            default -> {
                rows = feedRepository.findNewest(lim, off);
            }
        }

        var items = rows.stream().map(r -> {
            MarkerFeedItem it = new MarkerFeedItem();
            it.setMarkerId(r.getMarkerId());
            it.setTitle(r.getTitle());
            it.setLat(r.getLat());
            it.setLng(r.getLng());
            it.setThumbnailUrl(r.getThumbnailUrl());
            it.setAddress(r.getAddress());
            it.setDescription(r.getDescription());
            it.setCreatedAt(r.getCreatedAt());
            it.setRatingsCount(r.getRatingsCount());
            it.setAvgRating(r.getAvgRating());
            it.setCreatorUserId(r.getCreatorUserId());
            it.setCreatorUsername(r.getCreatorUsername());
            it.setCreatorAvatarUrl(r.getCreatorAvatarUrl());
            it.setDistanceKm(r.getDistanceKm());
            it.setRecentVotes(r.getRecentVotes());
            it.setWilson(r.getWilson());
            return it;
        }).toList();

        Integer nextOffset = items.size() < lim ? null : off + lim;
        return new FeedAndRankingResponse<>(items, nextOffset);
    }
}
