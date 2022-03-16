create table post_tagged_product
(
    post_id               varchar(255)       not null,
    product_id                  varchar(255) not null,
    primary key (post_id, product_id),

    post_id_open                  varchar(255) not null,

    constraint fk_post_tagged_product_product
        foreign key (product_id) references product_v3 (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;
