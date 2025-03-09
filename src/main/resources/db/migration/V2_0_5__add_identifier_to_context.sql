ALTER TABLE context
    add column if not exists device_identifier uuid;


ALTER TABLE context
    add column if not exists type varchar;