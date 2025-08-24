package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FeedResponse<T> {
    private List<T> items;
    private Integer nextOffset;
}
