package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import so.born.tracker.anime.HorribleParser.Torrent;

import com.google.common.base.Joiner;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

public class ReleasesFeed {

    private String title;
    private String guid;
    private List<SyndEntry> entries = new ArrayList<>();

    public ReleasesFeed(String title, String guid) {
        this.title = title;
        this.guid = guid;
    }

    public void addRelease(String name, Torrent torrent, Map<String, Torrent> altLinks) {
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle(name);
        entry.setLink(torrent.getLink());
        SyndContentImpl content = new SyndContentImpl();
        content.setType("text/html");
        String contentValue = String.format("<a href=\"%s\">%s (%s)</a>", torrent.getLink(), name, torrent.getSize());
        if (!altLinks.isEmpty()) {
            List<String> alts = altLinks.values().stream()
                    .map(t -> String.format("<a href=\"%s\">%s</a>", t.getLink(), t.getSize()))
                    .collect(Collectors.toList());
            contentValue = contentValue + "(" + Joiner.on(" | ").join(alts) + ")";
        }
        content.setValue(contentValue);
        entry.getContents().add(content);
        entries.add(entry);
    }

    public SyndFeed toFeed() {
        SyndFeed sf = new SyndFeedImpl();
        sf.setFeedType("rss_2.0");
        sf.setTitle(title);
        sf.setDescription(title);
        sf.setLink(guid);
        sf.setEntries(entries);
        return sf;
    }
}
