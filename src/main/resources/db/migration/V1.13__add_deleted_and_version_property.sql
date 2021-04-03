ALTER TABLE attendance ADD deleted boolean default false;
ALTER TABLE attendance ADD version bigint default 0;

ALTER TABLE attendance_by_admin ADD deleted boolean default false;
ALTER TABLE attendance_by_admin ADD version bigint default 0;

ALTER TABLE company ADD deleted boolean default false;
ALTER TABLE company ADD version bigint default 0;

ALTER TABLE employee ADD deleted boolean default false;
ALTER TABLE employee ADD version bigint default 0;

ALTER TABLE holiday ADD deleted boolean default false;
ALTER TABLE holiday ADD version bigint default 0;

ALTER TABLE note ADD deleted boolean default false;
ALTER TABLE note ADD version bigint default 0;

ALTER TABLE overtime ADD deleted boolean default false;
ALTER TABLE overtime ADD version bigint default 0;

ALTER TABLE payment ADD deleted boolean default false;
ALTER TABLE payment ADD version bigint default 0;

ALTER TABLE user ADD deleted boolean default false;
ALTER TABLE user ADD version bigint default 0;

ALTER TABLE user_role ADD deleted boolean default false;
ALTER TABLE user_role ADD version bigint default 0;

