package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.FeedAndRankingResponse;
import com.street_art_explorer.resource_server.dto.MarkerRankingItem;
import com.street_art_explorer.resource_server.dto.UserRankingItem;
import com.street_art_explorer.resource_server.dto.enums.Period;
import com.street_art_explorer.resource_server.service.RankingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@Validated
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/users")
    public FeedAndRankingResponse<UserRankingItem> getUsersRanking(
            @RequestParam(name = "period", required = false, defaultValue = "all") String period,
            @RequestParam(name = "limit", required = false, defaultValue = "50") @Min(1) @Max(100) Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") @PositiveOrZero Integer offset
    ) {
        return rankingService.getUserRanking(Period.from(period), limit, offset);
    }

    @GetMapping("/markers")
    public FeedAndRankingResponse<MarkerRankingItem> getMarkersRanking(
            @RequestParam(defaultValue = "all") String period,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) Integer limit,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset
    ) {
        return rankingService.getMarkerRanking(Period.from(period), limit, offset);
    }
}
