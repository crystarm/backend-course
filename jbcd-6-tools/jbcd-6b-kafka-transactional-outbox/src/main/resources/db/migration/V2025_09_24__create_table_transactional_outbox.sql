create table transactional_outbox
(
    id         uuid         not null primary key,
    topic      varchar(256) not null,
    key        varchar(256) not null,
    payload    text         not null,
    created_at timestamp default now() not null
);
