alter table user_detail
    add column if not exists chat_love_score integer not null default 50;
alter table user_detail
    add column if not exists chat_personality_score integer not null default 50;


alter table chat_message
    add column if not exists love_score integer not null default 50;
alter table chat_message
    add column if not exists personality_score integer not null default 50;