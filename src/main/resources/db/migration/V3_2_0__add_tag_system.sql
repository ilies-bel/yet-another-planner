-- Create tags table
CREATE TABLE tags
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT     NOT NULL,
    name        VARCHAR(50) NOT NULL,
    color       VARCHAR(7) DEFAULT '#808080',
    description TEXT,
    usage_count INTEGER    DEFAULT 0,
    created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN    DEFAULT FALSE,
    
    CONSTRAINT fk_tags_user FOREIGN KEY (user_id) REFERENCES user_detail (id) ON DELETE CASCADE,
    CONSTRAINT unique_user_tag UNIQUE (user_id, name)
);

-- Create indexes for tags table
CREATE INDEX idx_tags_user_id ON tags (user_id);
CREATE INDEX idx_tags_name ON tags (name);
CREATE INDEX idx_tags_user_archived ON tags (user_id, is_archived);

-- Create task_tags junction table
CREATE TABLE task_tags
(
    task_id     BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    
    PRIMARY KEY (task_id, tag_id),
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tags_assigned_by FOREIGN KEY (assigned_by) REFERENCES user_detail (id) ON DELETE SET NULL
);

-- Create indexes for task_tags table
CREATE INDEX idx_task_tags_task_id ON task_tags (task_id);
CREATE INDEX idx_task_tags_tag_id ON task_tags (tag_id);

-- Add function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update the updated_at column
CREATE TRIGGER update_tags_updated_at BEFORE UPDATE ON tags
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add indexes to task table for better tag-based queries (if not already exists)
-- Note: Tasks are user-scoped through context.userEmail, not direct user_id
CREATE INDEX IF NOT EXISTS idx_task_context_status ON task (context_id, status);