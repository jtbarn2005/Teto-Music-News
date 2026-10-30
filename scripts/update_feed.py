import json
import os
import sys
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta, timezone
from zoneinfo import ZoneInfo

TIME_ZONE = ZoneInfo("America/Denver")
ATOM = "http://www.w3.org/2005/Atom"
YT = "http://www.youtube.com/xml/schemas/2015"
MEDIA = "http://search.yahoo.com/mrss/"
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHANNELS_FILE = os.path.join(ROOT, "data", "channels.json")
OUTPUT = os.path.join(ROOT, "data", "videos.json")


def week_start(now):
    return (now - timedelta(days=(now.weekday() + 1) % 7)).replace(hour=0, minute=0, second=0, microsecond=0)


def parse_date(value):
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def child_text(parent, namespace, name, default=""):
    node = parent.find(f"{{{namespace}}}{name}")
    return node.text.strip() if node is not None and node.text else default


def fetch_channel(channel):
    url = "https://www.youtube.com/feeds/videos.xml?channel_id=" + channel["channelId"]
    request = urllib.request.Request(url, headers={"User-Agent": "TetoMusicNewsFeed/1.0"})
    with urllib.request.urlopen(request, timeout=30) as response:
        root = ET.fromstring(response.read())
    videos = []
    for entry in root.findall(f"{{{ATOM}}}entry"):
        published = child_text(entry, ATOM, "published")
        video_id = child_text(entry, YT, "videoId")
        group = entry.find(f"{{{MEDIA}}}group")
        thumbnail = ""
        description = ""
        if group is not None:
            description = child_text(group, MEDIA, "description")
            thumbnail_node = group.find(f"{{{MEDIA}}}thumbnail")
            if thumbnail_node is not None:
                thumbnail = thumbnail_node.attrib.get("url", "")
        videos.append({
            "videoId": video_id,
            "title": child_text(entry, ATOM, "title", "Untitled video"),
            "channel": channel.get("name", "Tracked channel"),
            "publishedAt": published[:10],
            "description": description,
            "thumbnail": thumbnail,
            "publishedDateTime": published,
        })
    return videos


def main():
    now = datetime.now(TIME_ZONE)
    if os.getenv("FORCE_UPDATE", "") != "1" and now.hour != 0:
        print(f"Not midnight in America/Denver ({now.isoformat()}); skipping.")
        return

    with open(CHANNELS_FILE, encoding="utf-8") as source:
        channels = json.load(source).get("channels", [])
    all_videos = []
    for channel in channels:
        try:
            all_videos.extend(fetch_channel(channel))
        except Exception as error:
            print(f"Could not read {channel.get('name', channel.get('channelId'))}: {error}", file=sys.stderr)

    start = week_start(now)
    month_start = now - timedelta(days=30)
    unique = {video["videoId"]: video for video in all_videos if video["videoId"]}
    ordered = sorted(unique.values(), key=lambda video: parse_date(video["publishedDateTime"]), reverse=True)
    weekly = [video for video in ordered if parse_date(video["publishedDateTime"]) >= start.astimezone(timezone.utc)]
    monthly = [video for video in ordered if parse_date(video["publishedDateTime"]) >= month_start.astimezone(timezone.utc)]
    for video in ordered:
        video.pop("publishedDateTime", None)

    document = {
        "timezone": "America/Denver",
        "source": "Public YouTube channel feeds; see data/channels.json",
        "weekStart": start.strftime("%Y-%m-%d"),
        "updatedAt": now.strftime("%Y-%m-%d %H:%M %Z"),
        "weeklyVideos": weekly,
        "popularVideos": monthly,
    }
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    with open(OUTPUT, "w", encoding="utf-8") as output:
        json.dump(document, output, indent=2, ensure_ascii=False)
        output.write("\n")
    print(f"Wrote {len(weekly)} weekly and {len(monthly)} monthly tracked videos for {start.date()}.")


if __name__ == "__main__":
    main()
