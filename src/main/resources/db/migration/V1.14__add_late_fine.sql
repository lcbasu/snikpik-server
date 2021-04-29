create table late_fine
(
    id                  varchar(255)    not null primary key,
    for_date           varchar(255)    not null,
    added_at             datetime        not null,

    hourly_late_fine_wage_in_paisa             bigint       not null,
    total_late_fine_amount_in_paisa             bigint       not null,
    total_late_fine_minutes             int       not null,

    employee_id                    varchar(255) not null,
    company_id                    varchar(255) not null,
    added_by_user_id           varchar(255)    not null,

    constraint fk_late_fine_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_late_fine_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_late_fine_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;
