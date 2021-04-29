ALTER TABLE product_order ADD address_id varchar(255) null;
ALTER TABLE product_order ADD CONSTRAINT fk_product_order_address_id
foreign key (address_id) references address (id);
