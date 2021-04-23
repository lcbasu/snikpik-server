ALTER TABLE product_order DROP CONSTRAINT fk_order_discount_id;
ALTER TABLE product_order DROP discount_id;
ALTER TABLE product_order ADD discount_id bigint null;
ALTER TABLE product_order ADD CONSTRAINT fk_product_order_discount_id
foreign key (discount_id) references discount (id);
