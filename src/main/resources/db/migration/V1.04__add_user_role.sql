create table user_role
(
    user_id                     varchar(255) not null,
    company_id                  varchar(255)       not null,
    role_type                   varchar(255) not null,
    primary key (user_id, company_id, role_type),
    constraint fk_user_role_company
        foreign key (company_id) references company (id),
    constraint fk_user_role_user
        foreign key (user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;
