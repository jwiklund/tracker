package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.List;

import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

public class NewReleasesFeed {

    private List<SyndEntry> entries = new ArrayList<>();

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
        sf.setTitle("New releases");
        sf.setDescription("New releases");
        sf.setLink("19610b12-0c77-48e5-871d-3045249238e5");
        sf.setEntries(entries);
        return sf;
    }
}
