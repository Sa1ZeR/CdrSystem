create table api_operators(
      id   serial not null,
      name varchar(32),
      primary key (id)
);

alter table if exists api_users
    add column operator_id integer;

alter table if exists api_users
    add constraint operator_id_fk
    foreign key (operator_id)
    references api_operators;

insert into api_operators(name) VALUES ('Ромашка');
insert into api_operators(name) VALUES ('Подсолнух');
insert into api_operators(name) VALUES ('Роза');