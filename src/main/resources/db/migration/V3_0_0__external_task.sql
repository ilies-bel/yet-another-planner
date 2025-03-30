create table if not exists external_task
(
    id         uuid,
    name       VARCHAR NOT NULL,
    natural_id varchar not null
)