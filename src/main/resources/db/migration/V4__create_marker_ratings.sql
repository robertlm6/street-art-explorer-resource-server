CREATE TABLE marker_ratings
(
    marker_id           INT       NOT NULL REFERENCES markers (id) ON DELETE CASCADE,
    auth_server_user_id INT       NOT NULL REFERENCES users (auth_server_user_id) ON DELETE RESTRICT,
    score               SMALLINT  NOT NULL CHECK (score BETWEEN 1 AND 5),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_marker_ratings PRIMARY KEY (marker_id, auth_server_user_id)
);

CREATE INDEX idx_marker_ratings_marker ON marker_ratings(marker_id);

CREATE OR REPLACE FUNCTION trg_markers_recalc_rating(p_marker_id INT)
RETURNS VOID AS $$
BEGIN
UPDATE markers m
SET avg_rating = COALESCE(sub.avg, 0),
    ratings_count = COALESCE(sub.cnt, 0),
    updated_at = CURRENT_TIMESTAMP
    FROM (
          SELECT marker_id, AVG(score)::NUMERIC(3,2) AS avg, COUNT(*)::INT AS cnt
            FROM marker_ratings
           WHERE marker_id = p_marker_id
           GROUP BY marker_id
      ) sub
WHERE m.id = p_marker_id;

IF NOT FOUND THEN
UPDATE markers
SET avg_rating = 0,
    ratings_count = 0,
    updated_at = CURRENT_TIMESTAMP
WHERE id = p_marker_id;
END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trg_after_ins_upd_del_ratings()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM trg_markers_recalc_rating(
        CASE WHEN TG_OP = 'DELETE' THEN OLD.marker_id ELSE NEW.marker_id END
    );
RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tgr_marker_ratings_aiud ON marker_ratings;
CREATE TRIGGER tgr_marker_ratings_aiud
    AFTER INSERT OR UPDATE OR DELETE ON marker_ratings
    FOR EACH ROW EXECUTE FUNCTION trg_after_ins_upd_del_ratings();
