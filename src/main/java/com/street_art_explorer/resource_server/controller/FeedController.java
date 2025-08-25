package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.dto.enums.FeedType;
import com.street_art_explorer.resource_server.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/markers")
    public FeedAndRankingResponse<MarkerFeedItem> getMarkersFeed(
            @RequestParam(defaultValue = "newest") String type,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Integer minVotes
    ) {
        FeedType t = FeedType.from(type);
        return feedService.getMarkersFeed(t, limit, offset, lat, lng, radiusKm, days, minVotes);
    }
}
