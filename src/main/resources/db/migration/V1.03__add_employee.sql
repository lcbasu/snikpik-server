create table employee
(
    id                  varchar(255)    not null primary key,
    name           varchar(255)    null,
    phone_number              varchar(255)    null,
    salary_type              varchar(255)    null,
    salary_amount_in_paisa             bigint       null,
    opening_balance_type              varchar(255)    null,
    opening_balance_in_paisa             bigint       null,
    balance_in_paisa_till_now             bigint       null,
    joined_at             datetime        null,
    left_at             datetime        null,
    created_for_user_id                    varchar(255) null,
    created_by_user_id                    varchar(255) null,
    company_id                    varchar(255) null,

    constraint fk_employee_created_for_user_id
        foreign key (created_for_user_id) references user (id),

    constraint fk_employee_created_by_user_id
        foreign key (created_by_user_id) references user (id),

    constraint fk_employee_creator_company_id
        foreign key (company_id) references company (id),
    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;

