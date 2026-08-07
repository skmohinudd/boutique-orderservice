ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS kafka_published_at TIMESTAMPTZ;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS rabbit_published_at TIMESTAMPTZ;
UPDATE outbox_events
SET kafka_published_at = COALESCE(kafka_published_at, published_at),
    rabbit_published_at = COALESCE(rabbit_published_at, published_at)
WHERE published_at IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_outbox_pending_transport
    ON outbox_events(created_at)
    WHERE kafka_published_at IS NULL OR rabbit_published_at IS NULL;
