
ALTER TABLE product ADD original_price_per_unit_in_paisa             bigint       null;
ALTER TABLE product ADD selling_price_per_unit_in_paisa             bigint       null;

create table product_variant
(
    id                  varchar(255)    not null primary key,
    title           varchar(255)    null,
#     // These should be used from Product Table
#     product_status           varchar(255)    null,
#     product_unit           varchar(255)    null,
    media_details             text null,
    tax_per_unit_in_paisa             bigint       null,
    original_price_per_unit_in_paisa             bigint       null,
    selling_price_per_unit_in_paisa             bigint       null,
    total_unit_in_stock             bigint       null,

    product_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_product_variant_company_id
        foreign key (company_id) references company (id),

    constraint fk_product_variant_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_product_variant_product_id
        foreign key (product_id) references product (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


ALTER TABLE cart_item DROP CONSTRAINT fk_cart_item_product_id;
ALTER TABLE cart_item DROP product_id;
ALTER TABLE cart_item ADD product_variant_id varchar(255) null;
ALTER TABLE cart_item ADD CONSTRAINT fk_cart_item_product_variant_id
    foreign key (product_variant_id) references product_variant (id);
