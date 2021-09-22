
ALTER TABLE company ADD default_shop_address_id varchar(255) null;
ALTER TABLE company ADD CONSTRAINT fk_company_default_shop_address_id
    foreign key (default_shop_address_id) references address (id);
