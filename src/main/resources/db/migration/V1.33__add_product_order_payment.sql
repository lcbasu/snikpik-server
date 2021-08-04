

create table product_order_payment
(
    id                  varchar(255)    not null primary key,
    payment_mode           varchar(255)    null,
    payment_status           varchar(255)    null,
    payment_config             text null,


    product_order_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_product_order_payment_company_id
        foreign key (company_id) references company (id),

    constraint fk_product_order_payment_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_product_order_payment_product_order_id
        foreign key (product_order_id) references product_order (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;


ALTER TABLE product_order ADD payment_mode             varchar(255)    null;
ALTER TABLE product_order ADD success_payment_id             varchar(255)    null;
