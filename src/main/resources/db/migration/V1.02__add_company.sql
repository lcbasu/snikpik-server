create table company
(
    id               bigint auto_increment primary key,
    name           varchar(255)    null,
    location              varchar(255)    null,
    salary_payment_schedule              varchar(255)    null,
    user_id                    varchar(255) not null,
    working_minutes             bigint       not null,
    constraint fk_company_creator_user_id
        foreign key (user_id) references user (id),
    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;

