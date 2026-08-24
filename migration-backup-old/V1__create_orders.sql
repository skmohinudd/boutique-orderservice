CREATE TABLE orders (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  idempotency_key VARCHAR(100) NOT NULL UNIQUE,
  status VARCHAR(30) NOT NULL,
  total NUMERIC(19,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  payment_id UUID,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id UUID NOT NULL,
  sku VARCHAR(80) NOT NULL,
  name VARCHAR(200) NOT NULL,
  unit_price NUMERIC(19,2) NOT NULL,
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  line_total NUMERIC(19,2) NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
