package com.street_art_explorer.resource_server.controller;

import com.street_art_explorer.resource_server.dto.MarkerRankingResponse;
import com.street_art_explorer.resource_server.dto.Period;
import com.street_art_explorer.resource_server.dto.UserRankingResponse;
import com.street_art_explorer.resource_server.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/users")
    public UserRankingResponse getUsersRanking(
            @RequestParam(name = "period", required = false, defaultValue = "all") String period,
            @RequestParam(name = "limit", required = false, defaultValue = "50") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset
    ) {
        limit = Math.max(1, Math.min(100, limit));
        offset = Math.max(0, offset);

        return rankingService.getUserRanking(Period.from(period), limit, offset);
    }

    @GetMapping("/markers")
    public MarkerRankingResponse getMarkersRanking(
            @RequestParam(defaultValue = "all") String period,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        limit = Math.max(1, Math.min(100, limit));
        offset = Math.max(0, offset);

        return rankingService.getMarkerRanking(Period.from(period), limit, offset);
    }
}
