create table users
(
    id            uuid                     not null primary key,
    email         varchar(320)             not null,
    password_hash varchar(100)             not null,
    created_at    timestamp with time zone not null,
    constraint uk_users_email unique (email)
);
