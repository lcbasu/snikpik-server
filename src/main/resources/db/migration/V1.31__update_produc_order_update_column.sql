
ALTER TABLE product_order DROP product_order_update;
ALTER TABLE product_order ADD product_order_state_before_update text null;
