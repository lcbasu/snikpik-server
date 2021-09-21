
create table product_order_state_change
(
    id                  varchar(255)    not null primary key,
    product_order_status           varchar(255)    null,
    state_change_at          datetime        null,
    product_order_state_change_data             text null,

    product_order_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_product_order_state_change_discount_id
        foreign key (product_order_id) references product_order (id),

    constraint fk_product_order_state_change_company_id
        foreign key (company_id) references company (id),

    constraint fk_product_order_state_change_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

