# Reddit Capture Feature Specification

## Overview

The Reddit Capture feature allows users to automatically import their saved Reddit posts as tasks in the planner application. This feature integrates with Reddit's OAuth API to securely access user's saved posts and convert them into actionable tasks.

## User Journey

1. User clicks "Connect Reddit Account" on the capture page
2. User is redirected to Reddit's OAuth authorization page
3. User grants permissions to access their saved posts
4. System fetches all saved posts from Reddit API
5. System creates tasks for each saved post (avoiding duplicates)
6. Tasks are created with status "CAPTURED" and tagged with "reddit"
7. User can review and organize imported tasks in the inbox

## Technical Requirements

### 1. Reddit OAuth Integration

#### OAuth Flow
- **Authorization URL**: `https://www.reddit.com/api/v1/authorize`
- **Token URL**: `https://www.reddit.com/api/v1/access_token`
- **Required Scopes**: `history` (to access saved posts)
- **Response Type**: `code`
- **Grant Type**: `authorization_code`

#### Required Reddit Application Setup
- Create Reddit app at https://www.reddit.com/prefs/apps
- App type: "web app"
- Redirect URI: `{BASE_URL}/api/reddit/callback`

### 2. Database Schema Changes

#### New Table: `reddit_integration`
```sql
CREATE TABLE reddit_integration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email VARCHAR(255) NOT NULL,
    reddit_username VARCHAR(100),
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_email) REFERENCES user_detail(email),
    UNIQUE(user_email)
);
```

#### Task Table Updates
```sql
-- Add source_url and source_type columns to existing task table
ALTER TABLE task ADD COLUMN source_url VARCHAR(500);
ALTER TABLE task ADD COLUMN source_type VARCHAR(50);

-- Create index for efficient duplicate checking
CREATE INDEX idx_task_source_url ON task(source_url) WHERE source_url IS NOT NULL;
```

#### New Enum: TaskStatus
```sql
-- Add CAPTURED status to existing task status enum
ALTER TYPE task_status ADD VALUE 'CAPTURED';
```

### 3. API Endpoints

#### POST `/api/reddit/authorize`
Initiates Reddit OAuth flow
```json
Response:
{
  "authorization_url": "https://www.reddit.com/api/v1/authorize?...",
  "state": "random_state_string"
}
```

#### GET `/api/reddit/callback`
Handles OAuth callback from Reddit
```
Query Parameters:
- code: Authorization code from Reddit
- state: State parameter for CSRF protection

Response: Redirect to frontend with success/error status
```

#### POST `/api/reddit/capture`
Triggers the capture process for authenticated user
```json
Response:
{
  "status": "success",
  "message": "Capture initiated",
  "job_id": "uuid"
}
```

#### GET `/api/reddit/capture/status/{job_id}`
Check capture job status
```json
Response:
{
  "status": "completed|in_progress|failed",
  "progress": {
    "total_posts": 150,
    "processed": 75,
    "created_tasks": 45,
    "duplicates_skipped": 30
  },
  "error_message": null
}
```

#### DELETE `/api/reddit/disconnect`
Disconnect Reddit integration
```json
Response:
{
  "status": "success",
  "message": "Reddit account disconnected"
}
```

#### GET `/api/reddit/status`
Get current Reddit integration status
```json
Response:
{
  "connected": true,
  "username": "reddit_user123",
  "last_capture": "2024-01-15T10:30:00Z",
  "total_imported": 45
}
```

### 4. Data Processing Logic

#### Saved Posts Fetching
- Use Reddit API endpoint: `GET /user/{username}/saved`
- Paginate through all saved posts (Reddit API has 25-100 items per page)
- Handle rate limiting (Reddit allows 60 requests per minute for OAuth apps)

#### Task Creation Rules
```
For each saved Reddit post:
1. Check if post already exists (by source_url)
2. If duplicate, skip and increment duplicate counter
3. If new, create task with:
   - name: Reddit post title (truncated to 255 chars)
   - description: Post content/selftext (first 1000 chars) + link to comments
   - status: CAPTURED
   - source_url: Reddit post permalink
   - source_type: "reddit"
   - context: null (user can assign later)
   - time_context: null
   - difficulty: null
   - due_date: null
   - creation_date: current timestamp
```

#### Duplicate Detection
```sql
SELECT id FROM task 
WHERE source_url = ? AND user_email = ?
LIMIT 1
```

### 5. Background Job Processing

#### Job Queue Implementation
- Use Spring Boot's `@Async` with custom thread pool
- Store job progress in Redis or database
- Implement job status tracking and cancellation

#### Error Handling
- API rate limiting: Implement exponential backoff
- Network failures: Retry mechanism with max attempts
- Invalid tokens: Refresh token or require re-authorization
- Partial failures: Continue processing and report errors

### 6. Security Considerations

#### Token Storage
- Encrypt access/refresh tokens before database storage
- Use application-level encryption key
- Rotate encryption keys periodically

#### CSRF Protection
- Generate random state parameter for OAuth flow
- Validate state parameter in callback

#### Rate Limiting
- Implement rate limiting for capture endpoints
- Respect Reddit's API rate limits
- Queue multiple capture requests if needed

### 7. Configuration

#### Environment Variables
```
REDDIT_CLIENT_ID=your_reddit_app_id
REDDIT_CLIENT_SECRET=your_reddit_app_secret
REDDIT_REDIRECT_URI=http://localhost:8080/api/reddit/callback
REDDIT_USER_AGENT=YetAnotherPlanner/1.0
TOKEN_ENCRYPTION_KEY=base64_encoded_key
```

#### Application Properties
```yaml
reddit:
  api:
    base-url: https://oauth.reddit.com
    auth-url: https://www.reddit.com/api/v1/authorize
    token-url: https://www.reddit.com/api/v1/access_token
  rate-limit:
    requests-per-minute: 60
  capture:
    max-posts-per-request: 100
    timeout-seconds: 300
```

### 8. Frontend Integration

#### Capture Page Updates
- Add connection status indicator
- Show capture progress with real-time updates
- Display capture statistics (total imported, last capture date)
- Provide disconnect option

#### User Flow States
1. **Not Connected**: Show "Connect Reddit Account" button
2. **Connecting**: Show loading state during OAuth flow
3. **Connected**: Show capture button and status information
4. **Capturing**: Show progress bar and real-time statistics
5. **Error**: Show error message with retry option

### 9. Testing Strategy

#### Unit Tests
- OAuth flow components
- Token encryption/decryption
- Task creation logic
- Duplicate detection

#### Integration Tests
- Complete capture flow with mock Reddit API
- Database operations
- Background job processing

#### Manual Testing
- OAuth flow with real Reddit account
- Capture of various post types (text, link, image)
- Error scenarios (invalid tokens, network failures)

### 10. Monitoring and Logging

#### Metrics to Track
- Successful captures per day
- Failed capture attempts
- Average capture duration
- Reddit API response times
- Token refresh frequency

#### Logging Requirements
- OAuth flow events (start, success, failure)
- Capture job lifecycle
- API errors and rate limiting events
- Security-related events (invalid states, token issues)

## Implementation Timeline

### Phase 1 (Week 1)
- Database schema updates
- Basic OAuth flow implementation
- Token management

### Phase 2 (Week 2)  
- Reddit API integration
- Task creation logic
- Duplicate detection

### Phase 3 (Week 3)
- Background job processing
- Progress tracking
- Error handling

### Phase 4 (Week 4)
- Frontend integration
- Testing and bug fixes
- Documentation

## Future Enhancements

1. **Selective Import**: Allow users to choose which saved posts to import
2. **Auto-sync**: Periodic background sync of new saved posts
3. **Advanced Filtering**: Import only posts from specific subreddits
4. **Comment Import**: Option to import saved comments as tasks
5. **Batch Operations**: Bulk actions on imported Reddit tasks
6. **Reddit Context**: Automatically assign subreddit as task context