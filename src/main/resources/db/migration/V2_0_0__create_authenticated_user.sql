CREATE TABLE account
(
    "id"            bigint PRIMARY KEY,
    "name"          varchar,
    "password_hash" varchar,
    "email"         varchar,
    "role"          varchar,
    "created_at"    timestamp
);