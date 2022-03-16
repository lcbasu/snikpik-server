
ALTER TABLE product_variant_v3 ADD max_delivery_time_in_seconds bigint null  default 1123200;

ALTER TABLE cart_item_v3 ADD max_delivery_date_time datetime null;
ALTER TABLE cart_item_v3 ADD promised_delivery_date_time datetime null;
ALTER TABLE cart_item_v3 ADD delivered_on_date_time datetime null;


ALTER TABLE product_order_v3 ADD min_of_max_delivery_date_time datetime null;
ALTER TABLE product_order_v3 ADD max_of_max_delivery_date_time datetime null;
ALTER TABLE product_order_v3 ADD min_of_promised_delivery_date_time datetime null;
ALTER TABLE product_order_v3 ADD max_of_promised_delivery_date_time datetime null;
ALTER TABLE product_order_v3 ADD first_cart_item_delivered_on_date_time datetime null;
ALTER TABLE product_order_v3 ADD last_cart_item_delivered_on_date_time datetime null;

