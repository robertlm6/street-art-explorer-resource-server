package com.street_art_explorer.resource_server.service.implementation;

import com.street_art_explorer.resource_server.dto.RatingSummary;
import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.entity.MarkerRating;
import com.street_art_explorer.resource_server.repository.MarkerRatingRepository;
import com.street_art_explorer.resource_server.repository.MarkerRepository;
import com.street_art_explorer.resource_server.service.MarkerRatingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarkerRatingServiceImpl implements MarkerRatingService {

    private final MarkerRatingRepository markerRatingRepository;
    private final MarkerRepository markerRepository;

    @Override
    @Transactional
    public RatingSummary createRating(Integer authId, Integer markerId, short score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        Marker marker = markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));

        if (marker.getAuthServerUserId().equals(authId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot rate your own marker");
        }

        MarkerRating markerRating = markerRatingRepository.findByMarkerIdAndAuthServerUserId(markerId, authId)
                .orElse(new MarkerRating(markerId, authId, score, null));
        markerRating.setScore(score);
        markerRatingRepository.saveAndFlush(markerRating);

        return summaryFromRatings(markerId);
    }

    @Override
    public Optional<Short> getRating(Integer authId, Integer markerId) {
        return markerRatingRepository.findByMarkerIdAndAuthServerUserId(markerId, authId)
                .map(MarkerRating::getScore);
    }

    @Override
    public RatingSummary getRatingSummary(Integer markerId) {
        Marker marker = markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));
        return new RatingSummary(marker.getAvgRating(), marker.getRatingsCount());
    }

    @Override
    @Transactional
    public RatingSummary deleteRating(Integer authId, Integer markerId) {
        markerRepository.findById(markerId)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found for id " + markerId));

        MarkerRating.PK pk = new MarkerRating.PK(markerId, authId);
        if (!markerRatingRepository.existsById(pk)) {
            throw new EntityNotFoundException("Marker rating not found for marker id " + markerId + " and authId " + authId);
        }

        markerRatingRepository.deleteById(pk);
        markerRatingRepository.flush();

        return summaryFromRatings(markerId);
    }

    private RatingSummary summaryFromRatings(Integer markerId) {
        var row = markerRatingRepository.avgAndCount(markerId);
        double avg = (row != null && row.getAvg() != null) ? row.getAvg() : 0.0;
        long cnt = (row != null && row.getCnt() != null) ? row.getCnt() : 0L;

        return new RatingSummary(
                BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP),
                (int) cnt
        );
    }
}
