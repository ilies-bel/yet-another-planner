CREATE TABLE if not exists user_detail
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR,
    email VARCHAR NOT NULL
);

alter table account
    add column "user_id" bigint;

alter table account
    add constraint fk_user
        foreign key (user_id)
            references user_detail (id)
            on delete cascade;


CREATE TABLE if not exists device
(
    id                    uuid PRIMARY KEY,
    name                  VARCHAR,
    type                  VARCHAR NOT NULL,
    platform              VARCHAR,
    last_platform_version VARCHAR,
    browser               VARCHAR,
    user_id               BIGINT,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES user_detail (id)
            ON DELETE CASCADE
);