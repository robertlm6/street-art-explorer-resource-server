package com.street_art_explorer.resource_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.street_art_explorer.resource_server.entity.MarkerRating;

@Repository
public interface MarkerRatingRepository extends JpaRepository<MarkerRating, Integer> {
}
