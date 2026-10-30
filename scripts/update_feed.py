import json
import os
import sys
import urllib.parse
import urllib.request
from datetime import datetime, timedelta, timezone
from zoneinfo import ZoneInfo

TIME_ZONE = ZoneInfo("America/Denver")
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUTPUT = os.path.join(ROOT, "data", "videos.json")
API_URL = "https://www.googleapis.com/youtube/v3/search"


def local_now():
    return datetime.now(TIME_ZONE)


def week_start(now):
    # Python weekday: Monday=0, Sunday=6.
    return (now - timedelta(days=(now.weekday() + 1) % 7)).replace(hour=0, minute=0, second=0, microsecond=0)


def fetch_videos(api_key, published_after, order):
    params = {
        "part": "snippet",
        "type": "video",
        "maxResults": "50",
        "order": order,
        "publishedAfter": published_after.astimezone(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
        "q": "Kasane Teto music",
        "key": api_key,
    }
    request = urllib.request.Request(API_URL + "?" + urllib.parse.urlencode(params), headers={"User-Agent": "TetoMusicNewsFeed/1.0"})
    with urllib.request.urlopen(request, timeout=30) as response:
        payload = json.load(response)
    videos = []
    for item in payload.get("items", []):
        snippet = item.get("snippet", {})
        video_id = item.get("id", {}).get("videoId")
        if not video_id:
            continue
        thumbnails = snippet.get("thumbnails", {})
        thumbnail = thumbnails.get("high", thumbnails.get("medium", thumbnails.get("default", {}))).get("url", "")
        videos.append({
            "videoId": video_id,
            "title": snippet.get("title", "Untitled video"),
            "channel": snippet.get("channelTitle", "Unknown channel"),
            "publishedAt": snippet.get("publishedAt", "")[:10],
            "description": snippet.get("description", ""),
            "thumbnail": thumbnail,
        })
    return videos


def main():
    now = local_now()
    force = os.getenv("FORCE_UPDATE", "") == "1"
    if not force and now.hour != 0:
        print(f"Not midnight in America/Denver ({now.isoformat()}); skipping.")
        return

    api_key = os.getenv("YOUTUBE_API_KEY")
    if not api_key:
        print("YOUTUBE_API_KEY is missing.", file=sys.stderr)
        sys.exit(1)

    start = week_start(now)
    weekly = fetch_videos(api_key, start, "date")
    popular = fetch_videos(api_key, now - timedelta(days=30), "viewCount")
    document = {
        "timezone": "America/Denver",
        "weekStart": start.strftime("%Y-%m-%d"),
        "updatedAt": now.strftime("%Y-%m-%d %H:%M %Z"),
        "weeklyVideos": weekly,
        "popularVideos": popular,
    }
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    with open(OUTPUT, "w", encoding="utf-8") as output:
        json.dump(document, output, indent=2, ensure_ascii=False)
        output.write("\n")
    print(f"Wrote {len(weekly)} weekly and {len(popular)} popular videos for {start.date()}.")


if __name__ == "__main__":
    main()
