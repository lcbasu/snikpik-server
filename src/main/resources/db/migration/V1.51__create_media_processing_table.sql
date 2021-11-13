
create table media_processing_detail
(
    id                  varchar(255)    not null primary key,

    resource_type           varchar(255)    null,
    resource_id           varchar(255)    null,

    -- ex: ending with mp4
    input_file_path             text null,
    input_file_present boolean null default false,

    -- ex: ending with m3u8
    output_file_path             text null,
    output_file_present boolean null default false,

    for_user_id           varchar(255) null,
    added_by_user_id           varchar(255) null,

    constraint fk_media_processing_details_for_user_id
        foreign key (for_user_id) references user (id),

    constraint fk_media_processing_details_added_by_user_id
        foreign key (added_by_user_id) references user (id),

    -- Common
    created_by          varchar(255)    null,
    created_at          datetime        null,
    last_modified_by    varchar(255)    null,
    last_modified_at    datetime        null,
    deleted             boolean         default false null,
    version             bigint          default 0
) engine = InnoDB;

