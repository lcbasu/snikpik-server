ALTER TABLE company ADD dk_shop_status varchar(255) null;
# https://www.w3schools.com/sql/sql_datatypes.asp
ALTER TABLE company ADD address text null;
ALTER TABLE company ADD username varchar(255) null;
ALTER TABLE company ADD total_order_amount_in_paisa bigint null;
ALTER TABLE company ADD total_store_view_count bigint null;
ALTER TABLE company ADD total_orders_count bigint null;
ALTER TABLE company ADD total_products_view_count bigint null;

create table unique_id
(
    id                  varchar(255)    not null primary key
) engine = InnoDB;


create table product
(
    id                  varchar(255)    not null primary key,
    title           varchar(255)    null,
    product_status           varchar(255)    null,
    product_unit           varchar(255)    null,
    media_details             text null,
    tax_per_unit_in_paisa             bigint       null,
    price_per_unit_in_paisa             bigint       null,
    total_unit_in_stock             bigint       null,

    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_product_company_id
        foreign key (company_id) references company (id),

    constraint fk_product_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table discount
(
    id                  varchar(255)    not null primary key,
    promo_code           varchar(255)    null,
    discount_type           varchar(255)    null,
    discount_amount             bigint       null,

    min_order_value_in_paisa             bigint       null,
    max_discount_amount_in_paisa             bigint       null,

    same_customer_count             bigint        default 1,
    visible_to_customer              boolean default true,

    start_at             datetime        null,
    end_at             datetime        null,

    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_discount_company_id
        foreign key (company_id) references company (id),

    constraint fk_discount_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table product_order
(
    id                  varchar(255)    not null primary key,

    discount_in_paisa             bigint       null,
    delivery_charge_in_paisa             bigint       null,
    total_tax_in_paisa             bigint       null,
    total_price_without_tax_in_paisa             bigint       null,
    total_price_payable_in_paisa             bigint       null,

    delivery_address             text null,

    order_status           varchar(255)    null,

    discount_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_order_discount_id
        foreign key (discount_id) references discount (id),

    constraint fk_order_company_id
        foreign key (company_id) references company (id),

    constraint fk_order_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table cart_item
(
    id                  varchar(255)    not null primary key,

    total_units             bigint       null,
    tax_per_unit_in_paisa             bigint       null,
    price_per_unit_in_paisa             bigint       null,
    total_tax_in_paisa             bigint       null,
    total_price_without_tax_in_paisa             bigint       null,

    product_order_id                    varchar(255) not null,
    product_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_cart_item_product_order_id
        foreign key (product_order_id) references product_order (id),

    constraint fk_cart_item_product_id
        foreign key (product_id) references product (id),

    constraint fk_cart_item_company_id
        foreign key (company_id) references company (id),

    constraint fk_cart_item_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table collection
(
    id                  varchar(255)    not null primary key,

    title           varchar(255)    null,
    sub_title           varchar(255)    null,
    media_details             text null,

    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_collection_company_id
        foreign key (company_id) references company (id),

    constraint fk_collection_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table company_customer
(
    company_id                  varchar(255)       not null,
    user_id                     varchar(255) not null,
    primary key (company_id, user_id),

    joined_at             datetime        null,
    first_order_at             datetime        null,
    last_order_at             datetime        null,

    total_orders_count             bigint       null,
    total_amount_spent             bigint       null,

    constraint fk_company_customer_company
        foreign key (company_id) references company (id),
    constraint fk_company_customer_user
        foreign key (user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table extra_charge_delivery
(
    company_id                  varchar(255)       not null,
    delivery_type                     varchar(255) not null,
    primary key (company_id, delivery_type),

    delivery_charge_per_order             bigint       null,
    delivery_charge_free_above             bigint       null,

    added_by_user_id           varchar(255) not null,

    constraint fk_extra_charge_delivery_company_id
        foreign key (company_id) references company (id),

    constraint fk_extra_charge_delivery_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table extra_charge_tax
(
    company_id                  varchar(255)       not null,
    tax_type                     varchar(255) not null,
    primary key (company_id, tax_type),

    tax_percentage             int       null,

    added_by_user_id           varchar(255) not null,

    constraint fk_extra_charge_tax_company_id
        foreign key (company_id) references company (id),

    constraint fk_extra_charge_tax_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table product_collection
(
    collection_id               varchar(255) not null,
    product_id                  varchar(255) not null,
    primary key (collection_id, product_id),

    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_product_collection_collection_id
        foreign key (collection_id) references collection (id),

    constraint fk_product_collection_product_id
        foreign key (product_id) references product (id),

    constraint fk_product_collection_company_id
        foreign key (company_id) references company (id),

    constraint fk_product_collection_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;
