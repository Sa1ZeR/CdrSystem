create table api_tariff (
    id serial not null,
    code varchar(3) not null,
    type varchar(255) not null,
    primary key (id)
);

create table api_users (
   id bigserial not null,
   balance float(53) not null,
   password varchar(64) not null,
   phone varchar(12) not null,
   tariff_id integer,
   primary key (id)
);

create table api_users_to_roles (
    user_id bigint not null,
    roles smallint not null
);

create table api_billing_data (
      id bigserial not null,
      total_cost float(53) not null,
      user_id bigint not null,
      primary key (id)
);

create table api_billing_data_report_data (
      billing_data_id bigint not null,
      report_data_id bigint not null,
      primary key (billing_data_id, report_data_id)
);

create table api_repot_data (
    id bigserial not null,
    call_type smallint not null,
    cost float(53) not null,
    duration bigint not null,
    end_time timestamp(6) not null,
    start_time timestamp(6) not null,
    primary key (id)
);

create table api_payments (
  id bigserial not null,
  amount float(53) not null,
  date_time timestamp(6),
  operation_type smallint,
  user_id bigint,
  primary key (id)
);

alter table if exists api_payments
    add constraint user_id_fk
    foreign key (user_id)
    references api_users;

create index phone_index on api_users (phone);

alter table if exists api_users
    add constraint UK_1ebqcbi8rejje5rj3lakxdgo3 unique (phone);

alter table if exists api_users
    add constraint user_tariff_fk
    foreign key (tariff_id)
    references api_tariff;

alter table if exists api_users_to_roles
    add constraint user_id_fk
    foreign key (user_id)
    references api_users;

alter table if exists api_billing_data_report_data
drop constraint if exists UK_ikqwjboylci1x4xelmlqjq31r;

alter table if exists api_billing_data_report_data
    add constraint UK_ikqwjboylci1x4xelmlqjq31r unique (report_data_id);

alter table if exists api_billing_data
    add constraint user_id_fk
    foreign key (user_id)
    references api_users;

alter table if exists api_billing_data_report_data
    add constraint report_data_fk
    foreign key (report_data_id)
    references api_repot_data;

alter table if exists api_billing_data_report_data
    add constraint billing_data_fk
    foreign key (billing_data_id)
    references api_billing_data;