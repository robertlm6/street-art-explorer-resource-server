package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Integer> {

    @Query(value = """
            SELECT m.*
            FROM markers m
            WHERE ST_Intersects(
                m.geom,
                ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)::geography
            )
            ORDER BY m.created_at DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Marker> findBBoxMarkers(@Param("minLat") double minLat,
                                 @Param("maxLat") double maxLat,
                                 @Param("minLng") double minLng,
                                 @Param("maxLng") double maxLng,
                                 @Param("limit") int limit);

    @Query(value = """
            SELECT m.*
            FROM markers m
            ORDER BY m.geom <-> ST_SetSRID(ST_MakePoint(:lng0, :lat0), 4326)::geography
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Marker> findNearMarkers(@Param("lat0") double lat0,
                                 @Param("lng0") double lng0,
                                 @Param("limit") int limit);
}
