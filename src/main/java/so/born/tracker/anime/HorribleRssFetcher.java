package so.born.tracker.anime;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import so.born.tracker.HorribleFetcher;

public class HorribleRssFetcher implements HorribleFetcher {

  public static final String URL = "https://nyaa.si/?page=rss&q=720p&c=1_2&f=2&u=HorribleSubs";
  private HorribleRssParser parser;

  public HorribleRssFetcher(HorribleRssParser parser) {
    this.parser = parser;
  }

  public HorribleRssFetcher() {
    this(new HorribleRssParser());
  }

  public List<HorribleLegacyParser.Episode> feed() throws IOException {
    try {
      SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(URL)));
      return parser.parse(feed);
    } catch (IllegalArgumentException | FeedException e) {
      throw new RuntimeException(e);
    }
  }
}
