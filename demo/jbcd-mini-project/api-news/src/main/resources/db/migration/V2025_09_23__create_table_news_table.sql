create table news_table
(
    id      uuid default gen_random_uuid() not null primary key,
    title   varchar(256)                   not null,
    message text                           not null
);
