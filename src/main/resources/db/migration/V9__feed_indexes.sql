CREATE INDEX IF NOT EXISTS idx_users_auth_server_user_id
    ON users (auth_server_user_id);

CREATE INDEX IF NOT EXISTS idx_marker_ratings_marker_created
    ON marker_ratings (marker_id, created_at);

CREATE INDEX IF NOT EXISTS idx_marker_photos_marker_created
    ON marker_photos (marker_id, created_at DESC);
