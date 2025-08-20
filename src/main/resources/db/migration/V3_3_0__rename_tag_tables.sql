-- Rename tags table to tag
ALTER TABLE tags RENAME TO tag;

-- Rename task_tags table to task_tag
ALTER TABLE task_tags RENAME TO task_tag;

-- Update foreign key constraint names to reflect new table names
ALTER TABLE task_tag RENAME CONSTRAINT fk_task_tags_task TO fk_task_tag_task;
ALTER TABLE task_tag RENAME CONSTRAINT fk_task_tags_tag TO fk_task_tag_tag;
ALTER TABLE task_tag RENAME CONSTRAINT fk_task_tags_assigned_by TO fk_task_tag_assigned_by;

-- Update index names to reflect new table names
ALTER INDEX idx_tags_user_id RENAME TO idx_tag_user_id;
ALTER INDEX idx_tags_name RENAME TO idx_tag_name;
ALTER INDEX idx_tags_user_archived RENAME TO idx_tag_user_archived;
ALTER INDEX idx_task_tags_task_id RENAME TO idx_task_tag_task_id;
ALTER INDEX idx_task_tags_tag_id RENAME TO idx_task_tag_tag_id;

-- Update the trigger name to reflect new table name
DROP TRIGGER update_tags_updated_at ON tag;
CREATE TRIGGER update_tag_updated_at BEFORE UPDATE ON tag
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Update foreign key reference in task_tag table
ALTER TABLE task_tag DROP CONSTRAINT fk_task_tag_tag;
ALTER TABLE task_tag ADD CONSTRAINT fk_task_tag_tag FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE;