create table attendance
(
    id                  varchar(255)    not null primary key,
    for_date           varchar(255)    null,
    punch_at             datetime        null,
    punch_type           varchar(255)    null,
    punch_by_user_id           varchar(255)    null,
    selfie_url           varchar(255)    null,
    selfie_type           varchar(255)    null,

    location_lat           double    null,
    location_long           double    null,
    location_name           varchar(255)    null,

    employee_id                    varchar(255) null,
    company_id                    varchar(255) null,


    constraint fk_attendance_punch_by_user_id
        foreign key (punch_by_user_id) references user (id),

    constraint fk_attendance_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_attendance_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;

