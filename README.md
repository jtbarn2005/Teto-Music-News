# Teto Music News

Teto Music News is a Java Android app that displays recent Kasane Teto music videos. The main screen shows videos published during the current Sunday-to-Saturday week, and a second screen shows tracked-channel videos from the current month. Tapping a video opens it in YouTube.

The app does not require users to provide a YouTube API key. A scheduled GitHub Actions workflow reads public YouTube channel feeds and publishes the results as a JSON file for the Android app to read.

## Instructions for Build and Use

Steps to build and/or run the software:

1. Push this project to a GitHub repository.
2. Add the YouTube channel IDs to `data/channels.json`:

   ```json
   {
     "channels": [
       {
         "channelId": "UCxxxxxxxxxxxxxxxxxxxxxx",
         "name": "Producer name"
       }
     ]
   }
   ```

3. Enable GitHub Actions and run **Update Teto video feed** manually from the repository’s **Actions** tab.
4. In `local.properties`, set the URL for the JSON file:

   ```properties
   TETO_FEED_URL=https://raw.githubusercontent.com/YOUR_GITHUB_USERNAME/YOUR_REPOSITORY/main/data/videos.json
   ```

5. Open the project folder in Android Studio and allow Gradle to sync.
6. Run the app on an Android emulator or connected Android device.

Instructions for using the software:

1. Launch **Teto Music News**.
2. View the current week’s tracked Kasane Teto videos on the home screen.
3. Tap **Refresh** to reload the latest published feed.
4. Tap **This month** to view tracked videos from the last 30 days.
5. Tap any video card to open the video in the YouTube app or browser.

The GitHub workflow runs once per day at midnight in the `America/Denver` time zone. The weekly list starts over every Sunday. GitHub Actions may occasionally start a scheduled job a few minutes late.

## Development Environment

To recreate the development environment, you need the following software and/or libraries with the specified versions:

* Android Studio with Android SDK Platform 35 installed
* Java Development Kit compatible with Android Gradle Plugin 8.5.2
* Android Gradle Plugin 8.5.2
* AndroidX AppCompat 1.7.0
* AndroidX RecyclerView 1.3.2
* Material Components for Android 1.12.0
* Glide 4.16.0
* Python 3.12 or newer for the scheduled feed script
* An Android emulator or Android device running Android 7.0/API 24 or newer

## Useful Websites to Learn More

I found these websites useful in developing this software:

* [Android Developers](https://developer.android.com/)
* [Android Studio Documentation](https://developer.android.com/studio/intro)
* [GitHub Actions Documentation](https://docs.github.com/en/actions)
* [YouTube Channel Feeds](https://www.youtube.com/feeds/videos.xml)
* [Android Intent Documentation](https://developer.android.com/reference/android/content/Intent)

## Future Work

The following items I plan to fix, improve, and/or add to this project in the future:

* [ ] Add more verified Kasane Teto producer channels to the feed configuration.
* [ ] Add a search or filter feature for tracked videos.
* [ ] Create a true adaptive Android launcher icon for different device shapes.
* [ ] Add automated tests for JSON parsing, date filtering, and Sunday weekly resets.
* [ ] Investigate a compliant way to provide true popularity rankings without exposing credentials in the Android app.
