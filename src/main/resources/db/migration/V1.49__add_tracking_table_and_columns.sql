
ALTER TABLE company ADD total_store_click_count bigint          default 0;
ALTER TABLE company ADD total_units_orders_count bigint          default 0;
ALTER TABLE company ADD total_products_click_count bigint          default 0;


ALTER TABLE collection ADD total_clicks_count bigint          default 0;
ALTER TABLE collection ADD total_orders_count bigint          default 0;
ALTER TABLE collection ADD total_products_view_count bigint          default 0;
ALTER TABLE collection ADD total_products_click_count bigint          default 0;
ALTER TABLE collection ADD total_units_orders_count bigint          default 0;


create table entity_tracking
(
    id                  varchar(255)    not null primary key,
    entity_type           varchar(255)    null,
    tracking_type           varchar(255)    null,
    tracking_data             text null,

    product_id                    varchar(255) not null,
    product_variant_id                    varchar(255) not null,
    collection_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_entity_tracking_product_id
        foreign key (product_id) references product (id),

    constraint fk_entity_tracking_product_variant_id
        foreign key (product_variant_id) references product_variant (id),

    constraint fk_entity_tracking_collection_id
        foreign key (collection_id) references collection (id),

        constraint fk_entity_tracking_company_id
        foreign key (company_id) references company (id),

    constraint fk_entity_tracking_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


ALTER TABLE product ADD total_clicks_count bigint          default 0;
ALTER TABLE product ADD total_variants_views_count bigint          default 0;
ALTER TABLE product ADD total_variants_clicks_count bigint          default 0;
ALTER TABLE product ADD total_orders_count bigint          default 0;


ALTER TABLE product_variant ADD total_views_count bigint          default 0;
ALTER TABLE product_variant ADD total_clicks_count bigint          default 0;
ALTER TABLE product_variant ADD total_order_amount_in_paisa bigint          default 0;
ALTER TABLE product_variant ADD total_units_orders_count bigint          default 0;
ALTER TABLE product_variant ADD total_orders_count bigint          default 0;



