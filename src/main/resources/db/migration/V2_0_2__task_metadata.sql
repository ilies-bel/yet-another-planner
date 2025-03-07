ALTER TABLE task add column creation_date timestamp;

ALTER TABLE task
    alter column difficulty
    TYPE varchar;
