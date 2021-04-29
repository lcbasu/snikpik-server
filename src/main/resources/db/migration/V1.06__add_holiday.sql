create table holiday
(
    company_id                  varchar(255)       not null,
    employee_id                  varchar(255)       not null,
    for_date                   varchar(255) not null,

    holiday_type           varchar(255)    null,

    added_by_user_id           varchar(255)    null,

    primary key (company_id, employee_id, for_date),

    constraint fk_holiday_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_holiday_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_holiday_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;
