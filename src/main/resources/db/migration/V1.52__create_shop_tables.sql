ALTER TABLE unique_id ADD prefix varchar(255) null;

create table user_v3
(
    id                  varchar(255)    not null primary key,

    uid                 varchar(255)    null,
    anonymous             boolean         default true null,
    verified             boolean         default true null,
    full_name           varchar(255)    null,
    notification_token           varchar(255)    null,
    notification_token_provider           varchar(255)    null,

    current_location_zipcode           varchar(255)    null,
    current_google_place_id           varchar(255)    null,
    current_location_id           varchar(255)    null,
    current_location_name           varchar(255)    null,
    current_location_lat           double    null,
    current_location_lng           double    null,
    current_location_locality           varchar(255)    null,
    current_location_sub_locality           varchar(255)    null,
    current_location_route           varchar(255)    null,
    current_location_city           varchar(255)    null,
    current_location_state           varchar(255)    null,
    current_location_country           varchar(255)    null,
    current_location_country_code           varchar(255)    null,
    current_location_complete_address           varchar(255)    null,


    permanent_location_zipcode           varchar(255)    null,
    permanent_google_place_id           varchar(255)    null,
    permanent_location_id           varchar(255)    null,
    permanent_location_name           varchar(255)    null,
    permanent_location_lat           double    null,
    permanent_location_lng           double    null,
    permanent_location_locality           varchar(255)    null,
    permanent_location_sub_locality           varchar(255)    null,
    permanent_location_route           varchar(255)    null,
    permanent_location_city           varchar(255)    null,
    permanent_location_state           varchar(255)    null,
    permanent_location_country           varchar(255)    null,
    permanent_location_country_code           varchar(255)    null,
    permanent_location_complete_address           varchar(255)    null,

    preferred_categories           text    null,

    absolute_mobile           varchar(255)    null,
    country_code           varchar(255)    null,
    handle           varchar(255)    null,
    email           varchar(255)    null,
    dp           text    null,
    cover_image           text    null,
    profiles           text    null,

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table discount_v3
(
    id                  varchar(255)    not null primary key,

    title           varchar(255)    null,
    description           varchar(255)    null,
    media_details           text null,

    existence_type           varchar(255)    null,

    discount_type           varchar(255)    null,
    discount_amount             double null,

    min_order_in_paisa             bigint       null,
    max_discount_in_paisa             bigint       null,

    same_customer_count             bigint        default 1,
    visible_to_customer              boolean default true,

    start_time             datetime        null,
    end_time             datetime        null,

    added_by_user_id           varchar(255) null,

    constraint fk_v3_discount_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table coupon_v3
(
    id                  varchar(255)    not null primary key,

    code           varchar(255)    null,

    title           varchar(255)    null,
    description           varchar(255)    null,
    media_details           text null,

    existence_type           varchar(255)    null,

    discount_type           varchar(255)    null,
    discount_amount             double null,

    min_order_in_paisa             bigint       null,
    max_discount_in_paisa             bigint       null,

    same_customer_count             bigint        default 1,
    visible_to_customer              boolean default true,

    start_time             datetime        null,
    end_time             datetime        null,

    added_by_user_id           varchar(255) null,

    constraint fk_v3_coupon_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table policy
(
    id                  varchar(255)    not null primary key,

    type           varchar(255)    null,

    title           varchar(255)    null,
    description           varchar(255)    null,
    media_details           text null,

    added_by_user_id           varchar(255) null,

    constraint fk_v3_policy_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table address_v3
(
    id                  varchar(255)    not null primary key,

    address_type                   varchar(255) not null,

    poc_name                   varchar(255) not null,
    poc_type                   varchar(255) not null,

    absolute_mobile           varchar(255)    null,
    country_code           varchar(255)    null,
    email           varchar(255)    null,

    complete_address           varchar(255)    null,

    flat_no_building_apartment_name           varchar(255)    null,
    street_locality           varchar(255)    null,

    route           varchar(255)    null,
    locality           varchar(255)    null,
    sub_locality           varchar(255)    null,

    city           varchar(255)    null,
    state           varchar(255)    null,
    country           varchar(255)    null,
    google_code           varchar(255)    null,

    zipcode           varchar(255)    null,
    latitude          double    null,
    longitude           double    null,

    added_by_user_id           varchar(255) null,
    constraint fk_v3_address_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

ALTER TABLE user_v3 ADD default_address_id varchar(255) null;
ALTER TABLE user_v3 ADD CONSTRAINT fk_v3_default_address_id
    foreign key (default_address_id) references address_v3 (id);


create table company_v3
(
    id                  varchar(255)    not null primary key,

    logo             text null,
    header_banner             text null,

    marketing_name                 varchar(255)    null,
    legal_name                 varchar(255)    null,
    date_of_establishment          datetime        null,

    total_store_view_count             bigint       null,
    total_store_click_count             bigint       null,


    total_brands_view_count             bigint       null,
    total_brands_click_count             bigint       null,


    total_products_view_count             bigint       null,
    total_products_click_count             bigint       null,


    total_order_amount_in_paisa             bigint       null,
    total_orders_count             bigint       null,
    total_units_orders_count             bigint       null,


    head_office_address_id                    varchar(255) null,
    constraint fk_v3_company_head_office_address_id
        foreign key (head_office_address_id) references address_v3 (id),

    communication_address_id                    varchar(255) null,
    constraint fk_v3_company_communication_address_id
        foreign key (communication_address_id) references address_v3 (id),


    billing_address_id                    varchar(255) null,
    constraint fk_v3_company_billing_address_id
        foreign key (billing_address_id) references address_v3 (id),

    added_by_user_id           varchar(255) null,
    constraint fk_v3_company_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;




create table brand
(
    id                  varchar(255)    not null primary key,

    handle                  varchar(255)    not null,

    logo             text null,
    header_banner             text null,

    marketing_name                 varchar(255)    null,
    legal_name                 varchar(255)    null,
    date_of_establishment          datetime        null,

    total_brands_view_count             bigint       null,
    total_brands_click_count             bigint       null,


    total_products_view_count             bigint       null,
    total_products_click_count             bigint       null,


    total_order_amount_in_paisa             bigint       null,
    total_orders_count             bigint       null,
    total_units_orders_count             bigint       null,


    company_id                    varchar(255) null,
    constraint fk_v3_brand_company_id
        foreign key (company_id) references company_v3 (id),


    added_by_user_id           varchar(255) null,
    constraint fk_v3_brand_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    constraint constraint_v3_brand_handle UNIQUE (handle),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_v3
(
    id                  varchar(255)    not null primary key,

--     title           varchar(255)    null,
--     media_details             text null,

    categories             text null,

    status           varchar(255)    null,


    product_unit           varchar(255)    null,


    brand_id                    varchar(255) null,
    company_id                    varchar(255) null,
    added_by_user_id           varchar(255) null,

    constraint fk_v3_product_brand_id
        foreign key (brand_id) references brand (id),

    constraint fk_v3_product_company_id
        foreign key (company_id) references company_v3 (id),

    constraint fk_v3_product_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table product_variant_v3
(
    id                  varchar(255)    not null primary key,

    status           varchar(255)    null,

    title           varchar(255)    null,
    description           text    null,
    media_details             text null,

    variant_type           varchar(255)    null,
    variant_infos             text null,
    properties             text null,
    specification             text null,

    categories             text null,

    view_in_room_allowed             boolean         default false null,

    shipped_from_address_id                    varchar(255) null,
    max_delivery_distance_in_km                    int null,
    delivers_over_india             boolean         default false null,
    replacement_acceptable             boolean         default false null,
    return_acceptable             boolean         default false null,
    cod_available             boolean         default false null,

    unit_quantity             bigint       null,
    min_order_unit_count             bigint       null,
    max_order_per_user             bigint       null,

    total_unit_in_stock             bigint       null,
    total_sold_units             bigint       null,
    total_sold_amount_in_paisa             bigint       null,
    total_orders_count             bigint       null,


    mrp_per_unit_in_paisa             bigint       null,
    selling_price_per_unit_in_paisa             bigint       null,


    product_id                    varchar(255) null,
    brand_id                    varchar(255) null,
    company_id                    varchar(255) null,
    added_by_user_id           varchar(255) null,

    constraint fk_v3_product_variant_shipped_from_address
        foreign key (shipped_from_address_id) references address_v3 (id),

    constraint fk_v3_product_variant_product_id
        foreign key (product_id) references product_v3 (id),

    constraint fk_v3_product_variant_brand_id
        foreign key (brand_id) references brand (id),

    constraint fk_v3_product_variant_company_id
        foreign key (company_id) references company_v3 (id),

    constraint fk_v3_product_variant_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


ALTER TABLE product_v3 ADD default_variant_id varchar(255) null;
ALTER TABLE product_v3 ADD CONSTRAINT fk_v3_product_default_variant
    foreign key (default_variant_id) references product_variant_v3 (id);


create table product_variant_category_v3
(
    product_variant_id                  varchar(255) not null,
    category               varchar(255)       not null,
    primary key (product_variant_id, category),

    constraint fk_v3_product_variant_category_product_variant
        foreign key (product_variant_id) references product_variant_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_category_v3
(
    product_id                  varchar(255) not null,
    category               varchar(255)       not null,
    primary key (product_id, category),

    constraint fk_v3_product_category_v3_product
        foreign key (product_id) references product_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_variant_vertical
(
    product_variant_id                  varchar(255) not null,
    vertical               varchar(255)       not null,
    primary key (product_variant_id, vertical),

    constraint fk_v3_product_variant_vertical_product_variant
        foreign key (product_variant_id) references product_variant_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table product_order_v3
(
    id                  varchar(255)    not null primary key,

    type           varchar(255)    null,

    total_cart_items             bigint       null,
    total_units_in_all_carts             bigint       null,

    order_status           varchar(255)    null,

    total_price_payable_in_paisa             bigint       null,

    total_discount_in_paisa             bigint       null,

    total_tax_in_paisa             bigint       null,

    delivery_charge_in_paisa             bigint       null,


    price_of_cart_items_without_tax_in_paisa             bigint       null,

    delivery_time_id           varchar(255)    null,


    payment_mode           varchar(255)    null,



    delivery_address_id                    varchar(255) null,
    coupon_id                    varchar(255) null,
    added_by_user_id           varchar(255) null,

    constraint fk_v3_order_delivery_address
        foreign key (delivery_address_id) references address_v3 (id),

    constraint fk_v3_order_coupon
        foreign key (coupon_id) references coupon_v3 (id),

    constraint fk_v3_order_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table company_address_v3
(
    company_id                  varchar(255)       not null,
    address_id                  varchar(255)       not null,
    primary key (company_id, address_id),

    name           varchar(255)    null,

    first_order_from_this_address_at             datetime        null,
    last_order_from_this_address_at             datetime        null,
    total_orders_from_this_address_count             bigint        null,

    constraint fk_v3_company_address_company
        foreign key (company_id) references company_v3 (id),
    constraint fk_v3_company_address_address
        foreign key (address_id) references address_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table user_address_v3
(
    user_id                  varchar(255) not null,
    address_id               varchar(255)       not null,
    primary key (user_id, address_id),

    name           varchar(255)    null,

    first_order_from_this_address_at             datetime        null,
    last_order_from_this_address_at             datetime        null,
    total_orders_from_this_address_count             bigint        null,


    constraint fk_v3_user_address_user
        foreign key (user_id) references user_v3 (id),
    constraint fk_v3_user_address_address
        foreign key (address_id) references address_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

create table brand_discount
(
    brand_id                  varchar(255) not null,
    discount_id               varchar(255)       not null,
    primary key (brand_id, discount_id),

    total_used_unit             bigint       null,
    total_available_unit             bigint       null,
    total_available_unit_per_user             bigint       null,

    constraint fk_v3_brand_discount_brand
        foreign key (brand_id) references brand (id),
    constraint fk_v3_brand_discount_discount
        foreign key (discount_id) references discount_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table company_discount_v3
(
    company_id                  varchar(255) not null,
    discount_id               varchar(255)       not null,
    primary key (company_id, discount_id),

    total_used_unit             bigint       null,
    total_available_unit             bigint       null,
    total_available_unit_per_user             bigint       null,

    constraint fk_v3_company_discount_company
        foreign key (company_id) references company_v3 (id),
    constraint fk_v3_company_discount_discount
        foreign key (discount_id) references discount_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_discount_v3
(
    product_id                  varchar(255) not null,
    discount_id               varchar(255)       not null,
    primary key (product_id, discount_id),

    total_used_unit             bigint       null,
    total_available_unit             bigint       null,
    total_available_unit_per_user             bigint       null,

    constraint fk_v3_product_discount_product
        foreign key (product_id) references product_v3 (id),
    constraint fk_v3_product_discount_discount
        foreign key (discount_id) references discount_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_variant_discount_v3
(
    product_variant_id                  varchar(255) not null,
    discount_id               varchar(255)       not null,
    primary key (product_variant_id, discount_id),

    total_used_unit             bigint       null,
    total_available_unit             bigint       null,
    total_available_unit_per_user             bigint       null,

    constraint fk_v3_product_variant_discount_product
        foreign key (product_variant_id) references product_variant_v3 (id),
    constraint fk_v3_product_variant_discount_discount
        foreign key (discount_id) references discount_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table brand_policy
(
    brand_id                  varchar(255) not null,
    policy_type               varchar(255)       not null,
    primary key (brand_id, policy_type),

    policy_id                  varchar(255) not null,

    constraint fk_v3_brand_policy_brand
        foreign key (brand_id) references brand (id),
    constraint fk_v3_brand_policy_policy
        foreign key (policy_id) references policy (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;



create table cart_item_v3
(
    id                  varchar(255)    not null primary key,

    total_units             bigint       null,

    tax_per_unit_in_paisa_paid             bigint       null,
    price_per_unit_in_paisa_paid             bigint       null,
    total_tax_in_paisa_paid             bigint       null,
    total_price_without_tax_in_paisa_paid             bigint       null,

    post_id                    varchar(255) null,

    product_order_id                    varchar(255) not null,
    product_variant_id                    varchar(255) not null,
    product_id                    varchar(255) not null,
    brand_id                    varchar(255) null,
    company_id                    varchar(255) null,
    added_by_user_id           varchar(255) not null,

    constraint fk_v3_cart_item_product_order_id
        foreign key (product_order_id) references product_order_v3 (id),

    constraint fk_v3_cart_item_product_variant_id
        foreign key (product_variant_id) references product_variant_v3 (id),

    constraint fk_v3_cart_item_product_id
        foreign key (product_id) references product_v3 (id),

    constraint fk_v3_cart_item_brand_id
        foreign key (brand_id) references brand (id),

    constraint fk_v3_cart_item_company_id
        foreign key (company_id) references company_v3 (id),

    constraint fk_v3_cart_item_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table save_for_later
(
    id                  varchar(255)    not null primary key,

    cart_item_id                    varchar(255) null,
    added_by_user_id           varchar(255) not null,

    constraint fk_v3_save_for_later_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    constraint fk_v3_save_for_later_cart_item_id
        foreign key (cart_item_id) references cart_item_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table bookmarked_products_v3
(
    id                  varchar(255)    not null primary key,

    bookmarked                    boolean         default false null,

    post_id                    varchar(255) null,

    product_variant_id                    varchar(255) not null,
    product_id                    varchar(255) not null,

    added_by_user_id           varchar(255) not null,

    constraint fk_v3_bookmarked_products_product_variant_id
        foreign key (product_variant_id) references product_variant_v3 (id),

    constraint fk_v3_bookmarked_products_product_id
        foreign key (product_id) references product_v3 (id),

    constraint fk_v3_bookmarked_products_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table company_policy
(
    company_id                  varchar(255) not null,
    policy_type               varchar(255)       not null,
    primary key (company_id, policy_type),

    policy_id                  varchar(255) not null,

    constraint fk_v3_company_policy_company
        foreign key (company_id) references company_v3 (id),
    constraint fk_v3_company_policy_policy
        foreign key (policy_id) references policy (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table company_user_role
(
    user_id                  varchar(255) not null,
    company_id                  varchar(255) not null,
    role_type               varchar(255)       not null,
    primary key (user_id, company_id, role_type),

    policy_id                  varchar(255) not null,

    constraint fk_v3_company_user_role_user
        foreign key (user_id) references user_v3 (id),

    constraint fk_v3_company_user_role_company
        foreign key (company_id) references company_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_order_payment_v3
(
    id                  varchar(255)    not null primary key,

    payment_mode           varchar(255)    null,

    payment_status           varchar(255)    null,

    payment_config             text null,


    product_order_id                    varchar(255) null,

    added_by_user_id           varchar(255) null,

    constraint fk_v3_product_order_payment_added_by_user_id
        foreign key (added_by_user_id) references user_v3 (id),

    constraint fk_v3_product_order_payment_product_order_id
        foreign key (product_order_id) references product_order_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


ALTER TABLE product_order_v3 ADD success_payment_id varchar(255) null;
ALTER TABLE product_order_v3 ADD CONSTRAINT fk_v3_order_success_payment
    foreign key (success_payment_id) references product_order_payment_v3 (id);


create table product_order_state_change_v3
(
    id                  varchar(255)    not null primary key,
    from_product_order_status           varchar(255)    null,
    to_product_order_status           varchar(255)    null,

    state_change_at          datetime        null,
    product_order_state_change_data             text null,

    product_order_id                    varchar(255) null,
    added_by_user_id           varchar(255) null,

    constraint fk_v3_product_order_state_change_product_order
        foreign key (product_order_id) references product_order_v3 (id),

    constraint fk_v3_product_order_state_change_added_by_user
        foreign key (added_by_user_id) references user_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

create table product_policy
(
    product_id                  varchar(255) not null,
    policy_type               varchar(255)       not null,
    primary key (product_id, policy_type),

    policy_id                  varchar(255) not null,

    constraint fk_v3_product_policy_product
        foreign key (product_id) references product_v3 (id),
    constraint fk_v3_product_policy_policy
        foreign key (policy_id) references policy (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


create table product_variant_policy
(
    product_variant_id                  varchar(255) not null,
    policy_type               varchar(255)       not null,
    primary key (product_variant_id, policy_type),

    policy_id                  varchar(255) not null,

    constraint fk_v3_product_variant_policy_product_variant
        foreign key (product_variant_id) references product_variant_v3 (id),
    constraint fk_v3_product_variant_policy_policy
        foreign key (policy_id) references policy (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

