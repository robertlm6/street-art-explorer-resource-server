package com.street_art_explorer.resource_server.projection;

public interface MarkerRankingRow {
    Integer getMarkerId();

    String getTitle();

    Double getLat();

    Double getLng();

    Integer getRatingsCount();

    Double getWilson();

    Double getAvgNorm();
}
