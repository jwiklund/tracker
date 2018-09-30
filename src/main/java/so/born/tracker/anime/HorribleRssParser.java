package so.born.tracker.anime;

import static java.util.Collections.singletonMap;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import so.born.tracker.anime.HorribleLegacyParser.Episode;
import so.born.tracker.anime.HorribleLegacyParser.Torrent;

public class HorribleRssParser {
  private static Logger LOG = LoggerFactory.getLogger(HorribleRssParser.class);
  private static final Pattern PATTERN = Pattern.compile("\\[([^\\]]+)\\] ([^\\[]+) - ([^ ]+) \\[([^\\]]+)\\]\\..*");

  public List<Episode> parse(SyndFeed feed) {
    List<Episode> episodes = new ArrayList<>();

    for (SyndEntry entry : feed.getEntries()) {
      parse(entry).ifPresent(episodes::add);
    }

    return episodes;
  }

  private Optional<Episode> parse(SyndEntry entry) {
    String title = entry.getTitle();
    Matcher matcher = PATTERN.matcher(title);
    if (!matcher.matches()) {
      LOG.warn("Entry does not match pattern {}", title);
      return Optional.empty();
    }
    Map<String, Torrent> torrents = singletonMap(matcher.group(4), new Torrent(title, matcher.group(4), entry.getUri()));
    Episode ep = new Episode(entry.getUri(), matcher.group(2), matcher.group(3), torrents);
    return Optional.of(ep);
  }
}
