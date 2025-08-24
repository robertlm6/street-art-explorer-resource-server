package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.UserApp;
import com.street_art_explorer.resource_server.projection.MarkerRankingRow;
import com.street_art_explorer.resource_server.projection.UserRankingRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RankingRepository extends JpaRepository<UserApp, Long> {
    @Query(value = """
            WITH params AS (
              SELECT COALESCE(:since, '-infinity'::timestamp) AS since
            ),
            contributors AS (
              SELECT DISTINCT m.auth_server_user_id AS userAuthId
              FROM markers m, params p
              WHERE m.created_at >= p.since
              UNION
              SELECT DISTINCT r.auth_server_user_id AS userAuthId
              FROM marker_ratings r, params p
              WHERE r.created_at >= p.since
            ),
            mc AS (
              SELECT m.auth_server_user_id AS userAuthId, COUNT(*)::bigint AS markersCreated
              FROM markers m, params p
              WHERE m.created_at >= p.since
              GROUP BY m.auth_server_user_id
            ),
            rc AS (
              SELECT r.auth_server_user_id AS userAuthId, COUNT(*)::bigint AS ratingsGiven
              FROM marker_ratings r, params p
              WHERE r.created_at >= p.since
              GROUP BY r.auth_server_user_id
            )
            SELECT u.id                                    AS userId,
                   u.username                              AS username,
                   u.avatar_url                            AS avatarUrl,
                   u.auth_server_user_id                   AS authServerUserId,
                   COALESCE(mc.markersCreated, 0)          AS markersCreated,
                   COALESCE(rc.ratingsGiven, 0)            AS ratingsGiven,
                   (COALESCE(mc.markersCreated,0) * 1.0
                    + COALESCE(rc.ratingsGiven,0) * 0.25)  AS score
            FROM contributors c
            LEFT JOIN mc ON mc.userAuthId = c.userAuthId
            LEFT JOIN rc ON rc.userAuthId = c.userAuthId
            LEFT JOIN users u ON u.auth_server_user_id = c.userAuthId
            ORDER BY score DESC, username ASC NULLS LAST
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<UserRankingRow> findUserRanking(
            @Param("since") LocalDateTime since,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            WITH params AS (
              SELECT COALESCE(:since, '-infinity'::timestamp) AS since
            ),
            scoped AS (
              SELECT r.marker_id                           AS markerId,
                     COUNT(*)::float                       AS n,
                     AVG(((r.score)::float - 1.0) / 4.0)  AS p_hat
              FROM marker_ratings r, params p
              WHERE r.created_at >= p.since
              GROUP BY r.marker_id
            ),
            scored AS (
              SELECT s.markerId,
                     s.n,
                     s.p_hat,
                     ( s.p_hat + (1.96^2)/(2*s.n)
                       - 1.96 * sqrt( (s.p_hat*(1 - s.p_hat) + (1.96^2)/(4*s.n)) / s.n )
                     ) / (1 + (1.96^2)/s.n)               AS wilson
              FROM scoped s
              WHERE s.n >= 1.0
            )
            SELECT m.id         AS markerId,
                   m.title      AS title,
                   m.lat        AS lat,
                   m.lng        AS lng,
                   (sc.n)::int  AS ratingsCount,
                   sc.wilson    AS wilson,
                   s.p_hat      AS avgNorm
            FROM scored sc
            JOIN scoped s   ON s.markerId = sc.markerId
            JOIN markers m  ON m.id = sc.markerId
            ORDER BY sc.wilson DESC, ratingsCount DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MarkerRankingRow> findMarkerRanking(
            @Param("since") LocalDateTime since,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
