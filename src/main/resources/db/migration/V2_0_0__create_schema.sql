CREATE TABLE "user"
(
    "id"            bigint PRIMARY KEY,
    "username"      varchar,
    "password_hash" varchar,
    "email"         varchar,
    "role"          varchar,
    "created_at"    timestamp
);