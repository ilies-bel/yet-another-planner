# Reddit OAuth Integration Flow Documentation

## Current Implementation Flow

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │  Spring Backend │    │   Reddit API    │    │   Database      │
│  (Capture Page) │    │   (YAP Server)  │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │                        │
         │                        │                        │                        │
    ┌────┴────┐                   │                        │                        │
    │ User    │                   │                        │                        │
    │ clicks  │                   │                        │                        │
    │"Connect │                   │                        │                        │
    │Reddit"  │                   │                        │                        │
    └────┬────┘                   │                        │                        │
         │                        │                        │                        │
         │ 1. GET /reddit/auth/url│                        │                        │
         ├────────────────────────┼────────────────────────┼────────────────────────┤
         │                        │                        │                        │
         │                        │ 2. generateAuthUrl()   │                        │
         │                        │   - Generate UUID state│                        │
         │                        │   - Store state->email │                        │
         │                        │   - Build Reddit URL   │                        │
         │                        │                        │                        │
         │ 3. Return authUrl      │                        │                        │
         │<───────────────────────┤                        │                        │
         │                        │                        │                        │
         │ 4. window.open(authUrl)│                        │                        │
         │ ┌──────────────────────┼────────────────────────┼──────────────────────┐ │
         │ │                      │                        │    NEW WINDOW        │ │
         │ │                      │                        │                      │ │
         │ │ 5. User redirected to Reddit OAuth             │                      │ │
         │ │ https://reddit.com/api/v1/authorize?           │                      │ │
         │ │ client_id=...&state=...&redirect_uri=...       │                      │ │
         │ │                      │                        │                      │ │
         │ │                      │                        │ 6. User logs in      │ │
         │ │                      │                        │    and authorizes    │ │
         │ │                      │                        │                      │ │
         │ │                      │                        │ 7. Reddit redirects  │ │
         │ │                      │                        │    to callback URL   │ │
         │ │                      │                        │                      │ │
         │ │ 8. GET /reddit/callback?code=...&state=...     │                      │ │
         │ │      ├─────────────────┼────────────────────────┼──────────────────────┘ │
         │ └──────┘                 │                        │                        │
         │                        │                        │                        │
         │                        │ 9. handleCallback()    │                        │
         │                        │   - Validate state     │                        │
         │                        │   - Exchange code      │ 10. POST /access_token │
         │                        │     for token          ├────────────────────────┤
         │                        │                        │ 11. Return tokens      │
         │                        │                        │<───────────────────────┤
         │                        │                        │                        │
         │                        │ 12. Save integration   │                        │
         │                        │     to database        ├────────────────────────┤
         │                        │                        │                        │
         │                        │ 13. Redirect to        │                        │
         │                        │     frontend success   │                        │
         │                        │                        │                        │
         │                        │                        │                        │
```

## Current Issues Identified

### 🚨 **MAJOR ISSUE: Broken OAuth Callback Flow**

The current implementation has a fundamental flow problem:

1. **Frontend opens popup** (`window.open`) to Reddit OAuth
2. **Reddit redirects to Spring backend** (`/reddit/callback`) 
3. **Spring backend redirects to frontend** (`http://localhost:3000/dashboard/capture`)
4. **❌ Frontend never knows the integration was successful!**

### Problems:

1. **Popup isolation**: The popup window redirects to frontend, but the parent window doesn't know
2. **No communication**: No way for popup to tell parent window about success/failure
3. **State loss**: User sees "Connect Reddit" button even after successful OAuth

## Recommended Fix Options

### Option 1: Post-Message Communication (Recommended)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │  Spring Backend │    │   Reddit API    │
│  (Parent Page)  │    │   (YAP Server)  │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         │ 1. Open popup          │                        │
         │ ┌──────────────────────┼────────────────────────┼───┐
         │ │     POPUP WINDOW     │                        │   │
         │ │                      │                        │   │
         │ │ 2. OAuth flow        │                        │   │
         │ │                      │ 3. After success,     │   │
         │ │                      │    redirect to         │   │
         │ │                      │    /auth/success.html  │   │
         │ │                      │                        │   │
         │ │ 4. success.html runs JavaScript:               │   │
         │ │    window.opener.postMessage({                 │   │
         │ │      type: 'REDDIT_AUTH_SUCCESS',              │   │
         │ │      data: { success: true }                   │   │
         │ │    }, '*')                                     │   │
         │ │    window.close()                              │   │
         │ └──────────────────────┼────────────────────────┼───┘
         │                        │                        │
         │ 5. Listen for message  │                        │
         │    and refresh data    │                        │
```

### Option 2: Polling Solution

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │  Spring Backend │
│  (Parent Page)  │    │   (YAP Server)  │
└─────────────────┘    └─────────────────┘
         │                        │
         │ 1. Open popup          │
         │ 2. Start polling       │
         │    every 2 seconds     │
         │                        │
         │ 3. GET /reddit/        │
         │    integration         │
         │<───────────────────────┤
         │                        │
         │ 4. If integration      │
         │    found, close popup  │
         │    and update UI       │
```

## Implementation Requirements

### Environment Variables Needed:
```bash
# Spring Backend (application.yml)
reddit:
  client-id: "your_reddit_app_id"
  client-secret: "your_reddit_app_secret"
  redirect-uri: "http://localhost:8080/reddit/callback"
```

### Reddit App Configuration:
- **App Type**: Web App
- **Redirect URI**: `http://localhost:8080/reddit/callback`
- **Permissions**: `history` (to read saved posts)

## Current Endpoint Status:

✅ **Working Endpoints:**
- `GET /reddit/auth/url` - Generates OAuth URL
- `GET /reddit/callback` - Handles OAuth callback
- `GET /reddit/integration` - Gets user's integration status
- `DELETE /reddit/integration` - Deletes integration
- `POST /reddit/capture` - Starts Reddit capture

❌ **Issue:** Frontend communication after OAuth completion