ALTER TABLE product ADD deleted boolean default false;
ALTER TABLE product ADD version bigint default 0;

ALTER TABLE discount ADD deleted boolean default false;
ALTER TABLE discount ADD version bigint default 0;

ALTER TABLE product_order ADD deleted boolean default false;
ALTER TABLE product_order ADD version bigint default 0;

ALTER TABLE cart_item ADD deleted boolean default false;
ALTER TABLE cart_item ADD version bigint default 0;

ALTER TABLE collection ADD deleted boolean default false;
ALTER TABLE collection ADD version bigint default 0;

ALTER TABLE company_customer ADD deleted boolean default false;
ALTER TABLE company_customer ADD version bigint default 0;

ALTER TABLE extra_charge_delivery ADD deleted boolean default false;
ALTER TABLE extra_charge_delivery ADD version bigint default 0;

ALTER TABLE extra_charge_tax ADD deleted boolean default false;
ALTER TABLE extra_charge_tax ADD version bigint default 0;

ALTER TABLE product_collection ADD deleted boolean default false;
ALTER TABLE product_collection ADD version bigint default 0;

ALTER TABLE address ADD deleted boolean default false;
ALTER TABLE address ADD version bigint default 0;

ALTER TABLE company_address ADD deleted boolean default false;
ALTER TABLE company_address ADD version bigint default 0;

ALTER TABLE user_address ADD deleted boolean default false;
ALTER TABLE user_address ADD version bigint default 0;
