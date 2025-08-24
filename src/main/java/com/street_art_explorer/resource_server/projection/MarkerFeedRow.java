package com.street_art_explorer.resource_server.projection;

import java.time.LocalDateTime;

public interface MarkerFeedRow {
    Integer getMarkerId();

    String getTitle();

    Double getLat();

    Double getLng();

    String getThumbnailUrl();

    String getAddress();

    String getDescription();

    LocalDateTime getCreatedAt();

    Integer getRatingsCount();

    Double getAvgRating();

    Integer getCreatorUserId();

    String getCreatorUsername();

    String getCreatorAvatarUrl();

    Double getDistanceKm();

    Integer getRecentVotes();

    Double getWilson();
}
