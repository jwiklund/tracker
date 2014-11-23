package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import so.born.tracker.anime.HorribleParser.Episode;
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

    public void addRelease(Episode ep) {
        Torrent torrent = ep.getLink();
        SyndEntryImpl entry = new SyndEntryImpl();
        SyndContentImpl content = new SyndContentImpl();

        entry.setTitle(ep.getName());
        entry.setLink(torrent.getLink());
        content.setType("text/html");

        String contentValue = String.format("<a href=\"%s\">%s (%s)</a>",
                torrent.getLink(), torrent.getName(), torrent.getSize());
        if (ep.getAniDBId().isPresent()) {
            contentValue = contentValue + String.format(" <a href=\"http://anidb.net/perl-bin/animedb.pl?show=anime&aid=%d\">AniDB</a> ", ep.getAniDBId().get());
        }
        if (!ep.getAltLinks().isEmpty()) {
            List<String> alts = ep.getAltLinks().values().stream()
                    .map(t -> String.format("<a href=\"%s\">%s</a>", t.getLink(), t.getSize()))
                    .collect(Collectors.toList());
            contentValue = contentValue + " (" + Joiner.on(" | ").join(alts) + ")";
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