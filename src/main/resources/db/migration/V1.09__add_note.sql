create table note
(
    id               bigint auto_increment primary key,
    for_date           varchar(255)    not null,
    added_at             datetime        not null,
    description           varchar(255)    not null,

    employee_id                    bigint not null,
    company_id                    bigint not null,
    added_by_user_id           varchar(255)    not null,

    constraint fk_note_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    constraint fk_note_employee_id
        foreign key (employee_id) references employee (id),

    constraint fk_note_company_id
        foreign key (company_id) references company (id),


    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null
) engine = InnoDB;

