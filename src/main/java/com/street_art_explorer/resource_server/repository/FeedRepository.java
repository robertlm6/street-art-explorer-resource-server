package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.Marker;
import com.street_art_explorer.resource_server.projection.MarkerFeedRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Marker, Integer> {

    @Query(value = """
            SELECT m.id    AS markerId,
                   m.title AS title,
                   m.lat   AS lat,
                   m.lng   AS lng,
                   COALESCE(cover.url, latest.url) AS thumbnailUrl,
            
                   m.address AS address,
                   m.description AS description,
                   m.created_at AS createdAt,
                   m.ratings_count AS ratingsCount,
                   (m.avg_rating)::double precision AS avgRating,
            
                   u.id AS creatorUserId,
                   u.username AS creatorUsername,
                   u.avatar_url AS creatorAvatarUrl
            
            FROM markers m
            LEFT JOIN users u ON u.auth_server_user_id = m.auth_server_user_id
            
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp.thumbnail_url, mp.secure_url, mp.url) AS url
              FROM marker_photos mp
              WHERE mp.id = m.cover_photo_id
            ) cover ON true
            
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp2.thumbnail_url, mp2.secure_url, mp2.url) AS url
              FROM marker_photos mp2
              WHERE mp2.marker_id = m.id
              ORDER BY mp2.created_at DESC
              LIMIT 1
            ) latest ON true
            
            ORDER BY m.created_at DESC, m.id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MarkerFeedRow> findNewest(@Param("limit") int limit,
                                   @Param("offset") int offset);

    @Query(value = """
            WITH p AS (
              SELECT ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS center,
                     (:radiusKm * 1000.0)::double precision     AS r_m
            )
            SELECT
              m.id    AS markerId,
              m.title AS title,
              m.lat   AS lat,
              m.lng   AS lng,
              COALESCE(cover.url, latest.url) AS thumbnailUrl,
            
              m.address AS address,
              m.description AS description,
              m.created_at AS createdAt,
              m.ratings_count AS ratingsCount,
              (m.avg_rating)::double precision AS avgRating,
            
              u.id AS creatorUserId,
              u.username AS creatorUsername,
              u.avatar_url AS creatorAvatarUrl,
            
              ST_Distance(m.geom::geography, p.center::geography) / 1000.0 AS distanceKm
            FROM markers m
            CROSS JOIN p
            LEFT JOIN users u ON u.auth_server_user_id = m.auth_server_user_id
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp.thumbnail_url, mp.secure_url, mp.url) AS url
              FROM marker_photos mp
              WHERE mp.id = m.cover_photo_id
            ) cover ON true
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp2.thumbnail_url, mp2.secure_url, mp2.url) AS url
              FROM marker_photos mp2
              WHERE mp2.marker_id = m.id
              ORDER BY mp2.created_at DESC
              LIMIT 1
            ) latest ON true
            WHERE ST_DWithin(m.geom::geography, p.center::geography, p.r_m)
            ORDER BY m.geom <-> p.center
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MarkerFeedRow> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            WITH params AS ( SELECT NOW() - (CAST(:days AS text) || ' days')::interval AS since ),
            stats AS (
              SELECT r.marker_id AS markerId,
                     COUNT(*)::float                       AS n,
                     AVG(((r.score)::float - 1.0) / 4.0)  AS p_hat
              FROM marker_ratings r, params p
              WHERE r.created_at >= p.since
              GROUP BY r.marker_id
            ),
            scored AS (
              SELECT s.markerId, s.n,
                (
                  s.p_hat + (1.96^2)/(2*s.n)
                  - 1.96 * sqrt( (s.p_hat*(1 - s.p_hat) + (1.96^2)/(4*s.n)) / s.n )
                ) / (1 + (1.96^2)/s.n) AS wilson
              FROM stats s
            )
            SELECT m.id    AS markerId,
                   m.title AS title,
                   m.lat   AS lat,
                   m.lng   AS lng,
                   COALESCE(cover.url, latest.url) AS thumbnailUrl,
            
                   m.address AS address,
                   m.description AS description,
                   m.created_at AS createdAt,
                   m.ratings_count AS ratingsCount,
                   (m.avg_rating)::double precision AS avgRating,
            
                   u.id AS creatorUserId,
                   u.username AS creatorUsername,
                   u.avatar_url AS creatorAvatarUrl,
            
                   (stats.n)::int AS recentVotes,
                   scored.wilson  AS wilson
            FROM stats
            JOIN scored ON scored.markerId = stats.markerId
            JOIN markers m ON m.id = stats.markerId
            LEFT JOIN users u ON u.auth_server_user_id = m.auth_server_user_id
            
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp.thumbnail_url, mp.secure_url, mp.url) AS url
              FROM marker_photos mp
              WHERE mp.id = m.cover_photo_id
            ) cover ON true
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp2.thumbnail_url, mp2.secure_url, mp2.url) AS url
              FROM marker_photos mp2
              WHERE mp2.marker_id = m.id
              ORDER BY mp2.created_at DESC
              LIMIT 1
            ) latest ON true
            
            ORDER BY (scored.wilson * LN(1 + stats.n)) DESC, stats.n DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MarkerFeedRow> findTrending(@Param("days") int days,
                                     @Param("limit") int limit,
                                     @Param("offset") int offset);

    @Query(value = """
            WITH stats AS (
              SELECT r.marker_id AS markerId,
                     COUNT(*)::float                       AS n,
                     AVG(((r.score)::float - 1.0) / 4.0)  AS p_hat
              FROM marker_ratings r
              GROUP BY r.marker_id
            ),
            filtered AS ( SELECT * FROM stats WHERE n >= :minVotes ),
            scored AS (
              SELECT f.markerId, f.n,
                (
                  f.p_hat + (1.96^2)/(2*f.n)
                  - 1.96 * sqrt( (f.p_hat*(1 - f.p_hat) + (1.96^2)/(4*f.n)) / f.n )
                ) / (1 + (1.96^2)/f.n) AS wilson
              FROM filtered f
            )
            SELECT m.id    AS markerId,
                   m.title AS title,
                   m.lat   AS lat,
                   m.lng   AS lng,
                   COALESCE(cover.url, latest.url) AS thumbnailUrl,
            
                   m.address AS address,
                   m.description AS description,
                   m.created_at AS createdAt,
                   m.ratings_count AS ratingsCount,
                   (m.avg_rating)::double precision AS avgRating,
            
                   u.id AS creatorUserId,
                   u.username AS creatorUsername,
                   u.avatar_url AS creatorAvatarUrl,
            
                   (filtered.n)::int AS recentVotes,
                   scored.wilson AS wilson
            FROM scored
            JOIN filtered ON filtered.markerId = scored.markerId
            JOIN markers m ON m.id = scored.markerId
            LEFT JOIN users u ON u.auth_server_user_id = m.auth_server_user_id
            
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp.thumbnail_url, mp.secure_url, mp.url) AS url
              FROM marker_photos mp
              WHERE mp.id = m.cover_photo_id
            ) cover ON true
            LEFT JOIN LATERAL (
              SELECT COALESCE(mp2.thumbnail_url, mp2.secure_url, mp2.url) AS url
              FROM marker_photos mp2
              WHERE mp2.marker_id = m.id
              ORDER BY mp2.created_at DESC
              LIMIT 1
            ) latest ON true
            
            ORDER BY scored.wilson DESC, filtered.n DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MarkerFeedRow> findTop(@Param("minVotes") int minVotes,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

}
