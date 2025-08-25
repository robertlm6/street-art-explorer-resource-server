package com.street_art_explorer.resource_server.projection;

public interface UserRankingRow {
    Integer getUserId();

    String getUsername();

    String getAvatarUrl();

    Integer getAuthServerUserId();

    Integer getMarkersCreated();

    Integer getRatingsGiven();

    Double getScore();
}
