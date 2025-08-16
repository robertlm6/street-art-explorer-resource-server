CREATE EXTENSION IF NOT EXISTS postgis;

ALTER TABLE markers
    ADD COLUMN IF NOT EXISTS geom geography(Point, 4326);

UPDATE markers
SET geom = ST_SetSRID(ST_MakePoint(lng, lat), 4326)::geography
WHERE geom IS NULL;

CREATE INDEX IF NOT EXISTS idx_markers_geom
    ON markers
    USING GIST (geom);

CREATE OR REPLACE FUNCTION update_geom_from_latlng()
RETURNS trigger AS $$
BEGIN
  NEW.geom = ST_SetSRID(ST_MakePoint(NEW.lng, NEW.lat), 4326)::geography;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_geom ON markers;

CREATE TRIGGER trg_update_geom
    BEFORE INSERT OR UPDATE OF lat, lng ON markers
    FOR EACH ROW EXECUTE FUNCTION update_geom_from_latlng();
