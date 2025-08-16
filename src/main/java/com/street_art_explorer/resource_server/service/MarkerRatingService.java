package com.street_art_explorer.resource_server.service;

import com.street_art_explorer.resource_server.dto.RatingSummary;

import java.util.Optional;

public interface MarkerRatingService {

    RatingSummary createRating(Integer authId, Integer markerId, short score);

    Optional<Short> getRating(Integer authId, Integer markerId);

    RatingSummary getRatingSummary(Integer markerId);

    RatingSummary deleteRating(Integer authId, Integer markerId);
}
