# Teto Music News

Java Android app for the CSE310 project. The Android app reads a daily JSON feed, so users never need a YouTube API key.

## Run it in Android Studio

1. Put this project in a GitHub repository.
2. Create a YouTube Data API v3 key in Google Cloud and add it to the repository as an Actions secret named `YOUTUBE_API_KEY`.
3. Enable GitHub Actions and run **Update Teto video feed** once manually.
4. Set the feed URL in `local.properties` before building the Android app:

   `TETO_FEED_URL=https://raw.githubusercontent.com/YOUR_GITHUB_USERNAME/YOUR_REPOSITORY/main/data/videos.json`

5. Open this folder in Android Studio and run it on an emulator or Android phone.

The workflow runs at midnight in America/Denver. It runs twice in UTC to handle daylight saving time, then the script keeps only the run that is actually midnight locally. The weekly feed starts over every Sunday and contains that week’s videos. The “Popular this month” screen reads the same feed and contains the last 30 days ordered by view count. Tapping a video opens its YouTube URL, which Android routes to the YouTube app when installed.
