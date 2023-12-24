create table if not exists pizza
(
    price       double precision,
    id          uuid not null
        primary key,
    description varchar(255),
    name        varchar(255)
);
