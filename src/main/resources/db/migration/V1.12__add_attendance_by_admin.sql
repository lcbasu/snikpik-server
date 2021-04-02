create table attendance_by_admin
(
    company_id                  bigint       not null,
    employee_id                  bigint       not null,
    for_date                   varchar(255) not null,

    working_minutes             bigint       not null,

    attendance_type           varchar(255)    null,

    added_by_user_id           varchar(255)    null,

    primary key (company_id, employee_id, for_date),

    constraint fk_attendance_by_admin_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_attendance_by_admin_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_attendance_by_admin_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;
