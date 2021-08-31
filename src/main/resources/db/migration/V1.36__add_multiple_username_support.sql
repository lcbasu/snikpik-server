
create table company_username
(
    id                  varchar(255)    not null primary key,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_company_username_company_id
        foreign key (company_id) references company (id),

    constraint fk_company_username_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

