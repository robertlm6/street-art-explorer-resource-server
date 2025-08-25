package com.street_art_explorer.resource_server.dto;

import java.util.List;

public record FeedAndRankingResponse<T>(
        List<T> items,
        Integer nextOffset) {
}
