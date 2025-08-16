package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.MarkerPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkerPhotoRepository extends JpaRepository<MarkerPhoto, Integer> {
    List<MarkerPhoto> findByMarkerIdOrderByIdAsc(Integer markerId);

    Optional<MarkerPhoto> findByIdAndMarkerId(Integer photoId, Integer markerId);
}
