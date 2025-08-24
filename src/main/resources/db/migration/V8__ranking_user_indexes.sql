CREATE INDEX IF NOT EXISTS idx_markers_user_created_at
    ON markers (auth_server_user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_marker_ratings_user_created_at
    ON marker_ratings (auth_server_user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_markers_created_at ON markers (created_at);
CREATE INDEX IF NOT EXISTS idx_marker_ratings_created_at ON marker_ratings (created_at);
