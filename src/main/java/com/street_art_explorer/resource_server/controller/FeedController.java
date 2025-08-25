package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerFeedItem;
import com.street_art_explorer.resource_server.dto.enums.FeedType;
import com.street_art_explorer.resource_server.service.FeedService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@Validated
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/markers")
    public FeedAndRankingResponse<MarkerFeedItem> getMarkersFeed(
            @RequestParam(defaultValue = "newest") String type,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer limit,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(required = false) @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @RequestParam(required = false) @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            @RequestParam(required = false) @DecimalMin("0.5") @DecimalMax("100.0") Double radiusKm,
            @RequestParam(required = false) @Min(1) @Max(90) Integer days,
            @RequestParam(required = false) @Min(1) @Max(50) Integer minVotes
    ) {
        FeedType t = FeedType.from(type);
        return feedService.getMarkersFeed(t, limit, offset, lat, lng, radiusKm, days, minVotes);
    }
}
