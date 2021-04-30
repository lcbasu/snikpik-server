
ALTER TABLE unique_id ADD created_by          varchar(255)    null;
ALTER TABLE unique_id ADD created_at          datetime        null;
ALTER TABLE unique_id ADD last_modified_by    varchar(255)    null;
ALTER TABLE unique_id ADD last_modified_at    datetime        null;
ALTER TABLE unique_id ADD deleted             boolean         default false null;
ALTER TABLE unique_id ADD version             bigint          default 0;
