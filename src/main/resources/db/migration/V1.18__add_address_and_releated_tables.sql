create table address
(
    id               bigint auto_increment primary key,
    line1           varchar(255)    null,
    line2           varchar(255)    null,
    zipcode           varchar(255)    null,
    city           varchar(255)    null,
    state           varchar(255)    null,
    country           varchar(255)    null,
    google_code           varchar(255)    null,
    latitude          double    null,
    longitude           double    null,
    phone           varchar(255)    null,

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;


create table company_address
(
    company_id                  bigint       not null,
    address_id                  bigint       not null,
    primary key (company_id, address_id),

    name           varchar(255)    null,

    first_order_from_this_address_at             datetime        null,
    last_order_from_this_address_at             datetime        null,
    total_orders_from_this_address_count             bigint        null,


    constraint fk_company_address_company
        foreign key (company_id) references company (id),
    constraint fk_company_address_address
        foreign key (address_id) references address (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;



create table user_address
(
    user_id                  varchar(255) not null,
    address_id               bigint       not null,
    primary key (user_id, address_id),

    name           varchar(255)    null,

    first_order_from_this_address_at             datetime        null,
    last_order_from_this_address_at             datetime        null,
    total_orders_from_this_address_count             bigint        null,


    constraint fk_user_address_user
        foreign key (user_id) references user (id),
    constraint fk_user_address_address
        foreign key (address_id) references address (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;
