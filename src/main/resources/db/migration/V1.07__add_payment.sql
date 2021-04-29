create table payment
(
    id                  varchar(255)    not null primary key,
    for_date           varchar(255)    not null DEFAULT '',
    added_at             datetime        not null DEFAULT CURRENT_TIMESTAMP,
    payment_type           varchar(255)    not null DEFAULT '',
    description           varchar(255)    null,
    amount_in_paisa             bigint       null,
    multiplier_used             int       null,

    employee_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255) not null,

    constraint fk_payment_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_payment_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_payment_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;

