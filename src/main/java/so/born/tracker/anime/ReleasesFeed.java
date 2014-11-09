package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.List;

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

    public void addRelease(String name) {
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle(name);
        String link = "https://track.born.so/horrible/track/" + name;
        entry.setLink(link);
        SyndContentImpl content = new SyndContentImpl();
        content.setType("text/html");
        content.setValue(String.format("<a href=\"%s\">%s</a>", link, name));
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
