-- For products that is added by user, Unbox takes a cut
ALTER TABLE product_variant_v3 ADD unbox_takes_commission_percentage double null default 5.00;
ALTER TABLE product_variant_v3 ADD unbox_takes_max_commission_in_paisa bigint null default 50000;

-- For unbox managed products that gets tagged on any post
ALTER TABLE product_variant_v3 ADD unbox_gives_commission_percentage double null default 10.00;
ALTER TABLE product_variant_v3 ADD unbox_gives_max_commission_in_paisa bigint null default 50000;
ALTER TABLE product_variant_v3 ADD managed_by_unbox boolean default false null;
