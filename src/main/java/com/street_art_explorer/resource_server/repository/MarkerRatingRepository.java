package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.MarkerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkerRatingRepository extends JpaRepository<MarkerRating, MarkerRating.PK> {

    interface AvgCnt {
        Double getAvg();

        Long getCnt();
    }

    @Query("""
              SELECT COALESCE(AVG(mr.score), 0.0) AS avg, COUNT(mr) AS cnt
              FROM MarkerRating mr
              WHERE mr.markerId = :markerId
            """)
    AvgCnt avgAndCount(@Param("markerId") Integer markerId);

    Optional<MarkerRating> findByMarkerIdAndAuthServerUserId(Integer markerId, Integer authServerUserId);
}
