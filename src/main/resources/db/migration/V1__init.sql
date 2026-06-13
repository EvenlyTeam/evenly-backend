create schema if not exists evenly;

create table evenly.users (
    id            uuid         primary key,
    email         varchar(256) not null unique,
    password_hash varchar(256) not null,
    created_at    timestamptz  not null
);

create table evenly.groups (
    id          uuid        primary key,
    name        varchar(50) not null,
    owner_id    uuid        not null references evenly.users (id),
    share_token varchar(64) unique,
    created_at  timestamptz not null,
    updated_at  timestamptz not null
);

create index idx_groups_owner_id on evenly.groups (owner_id);
