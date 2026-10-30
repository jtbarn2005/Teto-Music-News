# Teto Music News

Java Android app for the CSE310 project. The Android app reads a daily JSON feed, so neither the app nor the scheduled workflow needs a YouTube API secret.

## Run it in Android Studio

1. Put this project in a GitHub repository.
2. Add the YouTube channel IDs you want to track to `data/channels.json`, for example:

   `{ "channels": [{ "channelId": "UCxxxxxxxxxxxxxxxxxxxxxx", "name": "Producer name" }] }`

3. Enable GitHub Actions and run **Update Teto video feed** once manually.
4. Set the feed URL in `local.properties` before building the Android app:

   `TETO_FEED_URL=https://raw.githubusercontent.com/YOUR_GITHUB_USERNAME/YOUR_REPOSITORY/main/data/videos.json`

5. Open this folder in Android Studio and run it on an emulator or Android phone.

The workflow runs at midnight in America/Denver. It runs twice in UTC to handle daylight saving time, then the script keeps only the run that is actually midnight locally. The weekly feed starts over every Sunday and contains that week’s videos. The “This month” screen contains tracked-channel videos from the last 30 days, newest first. Public channel feeds do not provide reliable global search or view-count data, so this no-secret version tracks the channels listed in `data/channels.json` instead of claiming to find every YouTube upload. Tapping a video opens its YouTube URL, which Android routes to the YouTube app when installed.
