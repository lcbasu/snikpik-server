ALTER TABLE product_variant DROP color_info;
ALTER TABLE product_variant DROP size_info;

ALTER TABLE product_variant ADD variant_infos             text    null;
