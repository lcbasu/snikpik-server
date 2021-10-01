
ALTER TABLE user RENAME COLUMN mobile TO absolute_mobile;

ALTER TABLE employee RENAME COLUMN phone_number TO absolute_mobile;
ALTER TABLE employee ADD country_code varchar(255) null;

ALTER TABLE company RENAME COLUMN mobile TO absolute_mobile;

ALTER TABLE address RENAME COLUMN phone TO absolute_mobile;
ALTER TABLE address ADD country_code varchar(255) null;
