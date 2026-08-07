ALTER TABLE outbox_events
    ADD COLUMN IF NOT EXISTS retry_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_error TEXT,
    ADD COLUMN IF NOT EXISTS next_attempt_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_outbox_publish_schedule
    ON outbox_events(next_attempt_at, created_at)
    WHERE published_at IS NULL;
