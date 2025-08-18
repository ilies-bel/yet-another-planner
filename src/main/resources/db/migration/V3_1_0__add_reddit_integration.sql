-- Create reddit_integration table
CREATE TABLE reddit_integration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email VARCHAR(255) NOT NULL,
    reddit_username VARCHAR(100),
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reddit_user UNIQUE(user_email)
);

-- Add source tracking columns to task table
ALTER TABLE task ADD COLUMN source_url VARCHAR(500);
ALTER TABLE task ADD COLUMN source_type VARCHAR(50);

-- Create index for efficient duplicate checking
CREATE INDEX idx_task_source_url ON task(source_url) WHERE source_url IS NOT NULL;