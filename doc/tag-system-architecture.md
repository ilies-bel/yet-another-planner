# Tag System Architecture

## Overview

This document outlines the architecture for implementing a comprehensive tag system for task management in YAP (Yet
Another Planner). The tag system will enable users to categorize, filter, and organize tasks with flexible, user-defined
labels.

## Core Requirements

### Functional Requirements

- Users can create, edit, and delete custom tags
- Tasks can have multiple tags assigned
- Tags support color customization for visual organization
- Quick tag filtering and search capabilities
- Tag suggestions based on usage patterns
- Bulk tag operations (add/remove tags from multiple tasks)
- Tag hierarchy support (optional future enhancement)

### Non-Functional Requirements

- Scalable to support thousands of tags per user
- Consistent tag management across all interfaces
- Data integrity for tag-task relationships

## Database Schema

### Tags Table

```sql
CREATE TABLE tags
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER     NOT NULL,
    name        VARCHAR(50) NOT NULL,
    color       VARCHAR(7) DEFAULT '#808080', -- Hex color code
    description TEXT,
    usage_count INTEGER    DEFAULT 0,         -- Track popularity
    created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN    DEFAULT FALSE,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_tag (user_id, name),
    INDEX       idx_user_tags(user_id),
    INDEX       idx_tag_name(name)
);
```

### Task_Tags Junction Table

```sql
CREATE TABLE task_tags
(
    task_id     INTEGER NOT NULL,
    tag_id      INTEGER NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by INTEGER,

    PRIMARY KEY (task_id, tag_id),
    FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users (id) ON DELETE SET NULL,
    INDEX       idx_task_tags(task_id),
    INDEX       idx_tag_tasks(tag_id)
);
```

### Tag_Presets Table (Optional - for system/default tags)

```sql
CREATE TABLE tag_presets
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    color      VARCHAR(7) DEFAULT '#808080',
    category   VARCHAR(50), -- e.g., 'priority', 'project', 'personal'
    icon       VARCHAR(50), -- Icon identifier for UI
    is_system  BOOLEAN    DEFAULT FALSE,
    created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
```

### Modifications to Existing Task Table

```sql
-- Add indexes for better tag-based queries
ALTER TABLE tasks
    ADD INDEX idx_task_status_user(user_id, status);
ALTER TABLE tasks
    ADD FULLTEXT INDEX idx_task_search (name, description);
```

## API Endpoints

### Tag Management

#### Create Tag

```
POST /api/tags
{
    "name": "urgent",
    "color": "#FF0000",
    "description": "High priority items"
}
Response: 201 Created
```

#### Get User Tags

```
GET /api/tags
Query params:
  - search: string (optional)
  - archived: boolean (optional)
  - sort: 'name' | 'usage' | 'created' (optional)
Response: 200 OK
[
    {
        "id": 1,
        "name": "urgent",
        "color": "#FF0000",
        "description": "High priority items",
        "usageCount": 15,
        "createdAt": "2024-01-20T10:00:00Z"
    }
]
```

#### Update Tag

```
PUT /api/tags/{tagId}
{
    "name": "critical",
    "color": "#FF0000",
    "description": "Updated description"
}
Response: 200 OK
```

#### Delete Tag

```
DELETE /api/tags/{tagId}
Query params:
  - removeFromTasks: boolean (default: true)
Response: 204 No Content
```

### Task-Tag Operations

#### Assign Tags to Task

```
POST /api/tasks/{taskId}/tags
{
    "tagIds": [1, 2, 3]
}
Response: 200 OK
```

#### Remove Tags from Task

```
DELETE /api/tasks/{taskId}/tags
{
    "tagIds": [1, 2]
}
Response: 204 No Content
```

#### Get Tasks by Tags

```
GET /api/tasks
Query params:
  - tags: array of tag IDs or names
  - tagMode: 'any' | 'all' (default: 'any')
  - status: array of statuses (optional)
  - page: number
  - size: number
Response: 200 OK (Paginated task list)
```

#### Bulk Tag Operations

```
POST /api/tasks/bulk/tags
{
    "taskIds": [1, 2, 3, 4],
    "addTags": [1, 2],
    "removeTags": [3]
}
Response: 200 OK
```

### Tag Analytics

#### Get Tag Statistics

```
GET /api/tags/stats
Response: 200 OK
{
    "totalTags": 25,
    "mostUsed": [
        {"id": 1, "name": "urgent", "count": 45},
        {"id": 2, "name": "work", "count": 38}
    ],
    "recentlyCreated": [...],
    "tagDistribution": {
        "completed": 120,
        "inProgress": 45,
        "todo": 67
    }
}
```

## Frontend Integration

### TypeScript Types

```typescript
interface Tag {
    id: number;
    name: string;
    color: string;
    description?: string;
    usageCount: number;
    createdAt: string;
    updatedAt: string;
    isArchived: boolean;
}

interface TaskWithTags extends Task {
    tags: Tag[];
}

interface TagFilter {
    tags?: number[];
    tagMode?: 'any' | 'all';
}
```

### UI Components

#### Tag Pill Component

- Display tag with color background
- Click to filter by tag
- X button to remove tag
- Hover to show description tooltip

#### Tag Selector Component

- Multi-select dropdown with search
- Create new tag inline
- Color picker for new tags
- Recent/popular tags section

#### Tag Filter Bar

- Active tag filters display
- Quick clear all filters
- Tag combination mode toggle (AND/OR)

#### Tag Manager Modal

- CRUD operations for tags
- Bulk edit capabilities
- Tag merge functionality
- Usage statistics display

## Implementation Phases

### Phase 1: Core Functionality (MVP)

1. Database schema implementation
2. Basic CRUD API endpoints
3. Simple tag assignment to tasks
4. Basic tag filtering
5. Tag pill display on tasks

### Phase 2: Enhanced Features

1. Tag selector with search
2. Bulk operations
3. Tag colors and customization
4. Tag statistics
5. Keyboard shortcuts for quick tagging

### Phase 3: Advanced Features

1. Tag suggestions/auto-complete
2. Smart tag recommendations
3. Tag templates/presets
4. Tag hierarchies (parent-child relationships)
5. Tag rules/automation

## Performance Considerations

### Caching Strategy

- Cache user's tag list in Redis with 5-minute TTL
- Cache tag statistics with hourly refresh
- Invalidate cache on tag CRUD operations

### Query Optimization

- Use composite indexes for tag-based queries
- Implement database query result pagination
- Consider read replicas for tag analytics

### Scalability

- Limit tags per user (e.g., 500 tags)
- Limit tags per task (e.g., 20 tags)
- Implement tag archiving for unused tags
- Regular cleanup of orphaned tags

## Security Considerations

1. **Authorization**: Users can only manage their own tags
2. **Input Validation**: Sanitize tag names, enforce length limits
3. **Rate Limiting**: Limit tag creation to prevent spam
4. **XSS Prevention**: Escape tag names in UI rendering
5. **SQL Injection**: Use parameterized queries for all operations

## Migration Strategy

### Database Migration

```sql
-- Migration: 001_create_tags_tables.sql
BEGIN TRANSACTION;

-- Create tags table
CREATE TABLE tags
(...);

-- Create junction table
CREATE TABLE task_tags
(...);

-- Create indexes
CREATE INDEX ...;

COMMIT;
```

### Data Migration

- No existing tag data to migrate
- Consider importing project names as tags (optional)
- Provide UI for users to bulk-create initial tags

## Testing Strategy

### Unit Tests

- Tag CRUD operations
- Tag validation logic
- Tag-task association logic

### Integration Tests

- API endpoint testing
- Database constraint testing
- Concurrent tag operations

### E2E Tests

- Tag creation and assignment flow
- Tag filtering and search
- Bulk operations

## Monitoring & Analytics

### Key Metrics

- Average tags per task
- Most popular tags
- Tag creation rate
- Tag search performance
- Tag filter query times

### Logging

- Tag CRUD operations
- Failed tag operations
- Performance bottlenecks
- Usage patterns

## Future Enhancements

1. **Smart Tags**: Auto-tagging based on task content
2. **Tag Templates**: Predefined tag sets for common workflows
3. **Tag Sharing**: Share tag sets between team members
4. **Tag Analytics Dashboard**: Visual insights into tag usage
5. **Tag-based Automation**: Trigger actions based on tag combinations
6. **Natural Language Tagging**: Extract tags from task descriptions
7. **Tag Synonyms**: Map similar tags together
8. **Tag Permissions**: Control who can use certain tags (enterprise feature)