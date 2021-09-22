
ALTER TABLE cart_item ADD product_id varchar(255) null;
ALTER TABLE cart_item ADD CONSTRAINT fk_cart_item_product_id
    foreign key (product_id) references product (id);
