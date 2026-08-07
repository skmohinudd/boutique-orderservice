CREATE TABLE orders(id UUID PRIMARY KEY,user_id UUID NOT NULL,idempotency_key VARCHAR(160) NOT NULL,status VARCHAR(32) NOT NULL,total NUMERIC(19,2) NOT NULL,currency VARCHAR(3) NOT NULL,payment_id UUID,created_at TIMESTAMPTZ NOT NULL,updated_at TIMESTAMPTZ NOT NULL,CONSTRAINT uk_orders_idempotency UNIQUE(idempotency_key));
CREATE INDEX idx_orders_user_created ON orders(user_id,created_at DESC);
CREATE TABLE order_items(id UUID PRIMARY KEY,order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,product_id UUID NOT NULL,sku VARCHAR(255) NOT NULL,name VARCHAR(255) NOT NULL,unit_price NUMERIC(19,2) NOT NULL,quantity INTEGER NOT NULL CHECK(quantity>0),line_total NUMERIC(19,2) NOT NULL);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE TABLE outbox_events(id UUID PRIMARY KEY,aggregate_id UUID NOT NULL,event_type VARCHAR(100) NOT NULL,payload TEXT NOT NULL,created_at TIMESTAMPTZ NOT NULL,published_at TIMESTAMPTZ);
CREATE INDEX idx_outbox_unpublished ON outbox_events(created_at) WHERE published_at IS NULL;
