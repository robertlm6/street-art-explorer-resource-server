CREATE TABLE marker_photos
(
    id            SERIAL PRIMARY KEY,
    marker_id     INT          NOT NULL REFERENCES markers (id) ON DELETE CASCADE,
    public_id     VARCHAR(255) NOT NULL,
    url           TEXT         NOT NULL,
    secure_url    TEXT         NOT NULL,
    format        VARCHAR(20),
    width         INT,
    height        INT,
    bytes         INT,
    asset_id      VARCHAR(64),
    thumbnail_url TEXT,
    position      SMALLINT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_marker_photo_public UNIQUE (marker_id, public_id)
);

CREATE INDEX idx_marker_photos_marker ON marker_photos (marker_id);
CREATE INDEX idx_marker_photos_publicid ON marker_photos (public_id);
