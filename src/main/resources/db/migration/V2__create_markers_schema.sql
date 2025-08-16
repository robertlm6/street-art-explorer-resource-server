CREATE TABLE markers
(
    id                  SERIAL PRIMARY KEY,
    auth_server_user_id INT              NOT NULL,
    title               VARCHAR(120)     NOT NULL,
    description         TEXT,
    lat                 DOUBLE PRECISION NOT NULL,
    lng                 DOUBLE PRECISION NOT NULL,
    address             VARCHAR(255),
    avg_rating          NUMERIC(3, 2)    NOT NULL DEFAULT 0.00,
    ratings_count       INT              NOT NULL DEFAULT 0,
    created_at          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP        NOT NULL,
    CONSTRAINT fk_markers_user_authid
        FOREIGN KEY (auth_server_user_id)
            REFERENCES users (auth_server_user_id)
            ON UPDATE NO ACTION ON DELETE RESTRICT
);

-- Si usas @PreUpdate en JPA para updated_at, puedes omitir trigger.
-- Si prefieres DB-managed, añade el trigger igual que en users.

CREATE INDEX idx_markers_owner ON markers (auth_server_user_id);
CREATE INDEX idx_markers_created_at ON markers (created_at DESC);

CREATE INDEX idx_markers_lat ON markers (lat);
CREATE INDEX idx_markers_lng ON markers (lng);
