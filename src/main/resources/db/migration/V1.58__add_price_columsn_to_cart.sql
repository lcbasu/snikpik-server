ALTER TABLE cart_item_v3 ADD total_mrp_in_paisa bigint null default 0;
ALTER TABLE cart_item_v3 ADD total_selling_price_in_paisa bigint null default 0;

ALTER TABLE product_order_v3 ADD total_mrp_in_paisa bigint null default 0;
ALTER TABLE product_order_v3 ADD total_selling_price_in_paisa bigint null default 0;

