# Reddit Integration Setup Guide

This guide will walk you through setting up the Reddit OAuth integration to enable the Reddit capture feature in your YAP (Yet Another Planner) application.

## Prerequisites

- Reddit account
- YAP application running locally
- Access to Reddit's developer tools

## Step 1: Create a Reddit Application

1. **Navigate to Reddit Apps Page**
   - Go to https://www.reddit.com/prefs/apps
   - Log in to your Reddit account if not already logged in

2. **Create New Application**
   - Scroll down to the "Developed Applications" section
   - Click the **"Create App"** or **"Create Another App"** button

3. **Fill Application Details**
   - **Name**: Enter a name for your app (e.g., "YAP Task Planner")
   - **App type**: Select **"web app"**
   - **Description**: (Optional) Brief description of your app
   - **About URL**: (Optional) Leave blank or add your app's URL
   - **Redirect URI**: Enter `http://localhost:8080/api/reddit/callback`
     - ⚠️ **Important**: This must match exactly what's in your application.yml
   - **User Agent**: Will be automatically generated

4. **Create the Application**
   - Click **"Create app"** button
   - Reddit will create your application and show you the credentials

## Step 2: Obtain Your Credentials

After creating the app, you'll see:

1. **Client ID**: A string of characters below the app name (looks like: `ABC123def456`)
2. **Client Secret**: Click **"secret"** to reveal the client secret (looks like: `XYZ789abc123-LONG_STRING`)

⚠️ **Keep these credentials secure and never commit them to version control!**

## Step 3: Configure Environment Variables

### Option A: Using Environment Variables (Recommended)

Set these environment variables in your system:

```bash
export REDDIT_CLIENT_ID="your_actual_client_id_here"
export REDDIT_CLIENT_SECRET="your_actual_client_secret_here"
export REDDIT_REDIRECT_URI="http://localhost:8080/api/reddit/callback"
```

**For macOS/Linux (.bashrc or .zshrc):**
```bash
echo 'export REDDIT_CLIENT_ID="your_actual_client_id_here"' >> ~/.bashrc
echo 'export REDDIT_CLIENT_SECRET="your_actual_client_secret_here"' >> ~/.bashrc
echo 'export REDDIT_REDIRECT_URI="http://localhost:8080/api/reddit/callback"' >> ~/.bashrc
source ~/.bashrc
```

**For Windows (Command Prompt):**
```cmd
setx REDDIT_CLIENT_ID "your_actual_client_id_here"
setx REDDIT_CLIENT_SECRET "your_actual_client_secret_here"
setx REDDIT_REDIRECT_URI "http://localhost:8080/api/reddit/callback"
```

### Option B: Using application.yml (Not Recommended for Production)

Edit `/src/main/resources/application.yml`:

```yaml
reddit:
  client-id: "your_actual_client_id_here"
  client-secret: "your_actual_client_secret_here"
  redirect-uri: "http://localhost:8080/api/reddit/callback"
```

⚠️ **Warning**: Never commit real credentials to version control!

## Step 4: Verify Configuration

1. **Restart the Backend**
   ```bash
   mvn spring-boot:run
   ```

2. **Check Application Logs**
   - Look for any configuration errors in the startup logs
   - The application should start without Reddit-related errors

3. **Test the Configuration**
   - Go to `http://localhost:3000/dashboard/capture`
   - Click "Connect Reddit Account"
   - You should be redirected to Reddit's authorization page

## Step 5: Testing the Integration

1. **Connect Your Reddit Account**
   - Navigate to the capture page in your app
   - Click "Connect Reddit Account"
   - Authorize the application on Reddit
   - You should be redirected back to your app

2. **Test Capturing Saved Posts**
   - Make sure you have some saved posts on Reddit
   - Click "Capture Saved Posts" in your app
   - Check if tasks are created from your Reddit saved posts

## Troubleshooting

### Common Issues

**1. "Invalid redirect_uri" Error**
- Ensure the redirect URI in Reddit app settings exactly matches the one in your config
- Check for trailing slashes or http vs https mismatches

**2. "Invalid client_id" Error**
- Double-check your client ID from Reddit
- Ensure no extra spaces or characters

**3. "Access denied" Error**
- Make sure you're using the correct client secret
- Verify your app is set as "web app" type

**4. Backend fails to start**
- Check if environment variables are properly set
- Restart your terminal/IDE after setting environment variables

**5. No saved posts imported**
- Ensure you have saved posts on your Reddit account
- Check the backend logs for any API errors
- Verify your Reddit account has the necessary permissions

### Debugging Steps

1. **Check Environment Variables**
   ```bash
   echo $REDDIT_CLIENT_ID
   echo $REDDIT_CLIENT_SECRET
   echo $REDDIT_REDIRECT_URI
   ```

2. **Verify Application Configuration**
   - Check if the values appear correctly in application startup logs
   - Look for any configuration-related error messages

3. **Check Reddit App Settings**
   - Go back to https://www.reddit.com/prefs/apps
   - Verify your app configuration matches this guide

4. **Network Issues**
   - Ensure your local server can reach Reddit's API
   - Check if any firewall is blocking the requests

## Security Notes

1. **Environment Variables**: Always use environment variables for credentials in production
2. **HTTPS**: Use HTTPS for redirect URIs in production environments
3. **Secret Rotation**: Regularly rotate your Reddit app secret
4. **Scope Limitation**: The app only requests access to saved posts (minimal required permissions)

## Production Deployment

When deploying to production:

1. **Update Redirect URI**
   - Change the redirect URI in both Reddit app settings and your production config
   - Use your production domain: `https://yourdomain.com/api/reddit/callback`

2. **Use Secure Environment Variables**
   - Set environment variables in your deployment platform
   - Never hardcode credentials in application.yml for production

3. **HTTPS Required**
   - Reddit requires HTTPS for production redirect URIs
   - Ensure your production environment supports HTTPS

## Support

If you encounter issues:

1. Check the backend logs for detailed error messages
2. Verify all steps in this guide have been followed correctly
3. Consult Reddit's API documentation: https://www.reddit.com/dev/api/
4. Check the project's GitHub issues for similar problems

## API Endpoints Reference

Once configured, these endpoints will be available:

- `GET /api/reddit/auth-url` - Get Reddit authorization URL
- `GET /api/reddit/callback` - Handle OAuth callback
- `POST /api/reddit/capture` - Start capturing saved posts
- `GET /api/reddit/integration` - Get integration status
- `DELETE /api/reddit/integration` - Disconnect Reddit account

The frontend will automatically use these endpoints when you interact with the capture page.