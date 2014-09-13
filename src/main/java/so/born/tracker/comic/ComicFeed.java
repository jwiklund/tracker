package so.born.tracker.comic;

import java.util.ArrayList;
import java.util.List;

import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

public class ComicFeed {
    private String guid;
    private String name;
    private List<SyndEntry> entries = new ArrayList<>();

    public ComicFeed(String guid, String name) {
        this.guid = guid;
        this.name = name;
    }

    public void addEntry(String imageUrl) {
        addEntry(imageUrl, null);
    }

    public void addEntry(String imageUrl, String title) {
        if (title == null) {
            title = "Comic of the Day";
        }

        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle(title);
        entry.setLink(imageUrl);
        SyndContentImpl content = new SyndContentImpl();
        content.setType("text/html");
        content.setValue("<img src=" + imageUrl + ">");
        entry.getContents().add(content);
        entries.add(entry);
    }

    public SyndFeed toFeed() {
        SyndFeed sf = new SyndFeedImpl();
        sf.setFeedType("rss_2.0");
        sf.setTitle(name);
        sf.setDescription(name);
        sf.setLink(guid);
        sf.setEntries(entries);
        return sf;
    }
}
