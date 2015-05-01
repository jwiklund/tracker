package so.born.tracker.anime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import so.born.tracker.anime.HorribleParser.Episode;
import so.born.tracker.anime.HorribleParser.Torrent;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

public class ReleasesFeed {

    private String title;
    private String guid;
    private List<SyndEntry> entries = new ArrayList<>();
    private AniDB anidb;

    public ReleasesFeed(AniDB anidb, String title, String guid) {
        this.anidb = anidb;
        this.title = title;
        this.guid = guid;
    }

    public void addRelease(Episode ep) {
        SyndEntryImpl entry = new SyndEntryImpl();
        SyndContentImpl content = new SyndContentImpl();

        entry.setTitle(ep.getName());
        entry.setLink(ep.getLink().getLink());
        content.setType("text/html");

        content.setValue(content(anidb, ep));
        entry.getContents().add(content);
        entries.add(entry);
    }

    public static String content(AniDB anidb, Episode ep) {
        Torrent torrent = ep.getLink();
        String contentValue = String.format("<a href=\"%s\">%s (%s)</a>",
                torrent.getLink(), torrent.getName(), torrent.getSize());
        Map<String, String> links = new TreeMap<String, String>(new LinkSorter());
        for (Map.Entry<String, Torrent> link : ep.getAltLinks().entrySet()) {
            links.put(link.getValue().getSize(), link.getValue().getLink());
        }
        Optional<Long> maybeAnidb = anidb.lookupFirst(ep.getName());
        if (maybeAnidb.isPresent()) {
            links.put("AniDB", "http://anidb.net/perl-bin/animedb.pl?show=anime&aid=" + maybeAnidb.get());
        } else {
            try {
                String encoded = URLEncoder.encode(ep.getName(), "UTF-8");
                links.put("AniDB", "http://anidb.net/perl-bin/animedb.pl?show=search&do.search=search&adb.search=" + encoded);
            } catch (UnsupportedEncodingException e) {
                throw Throwables.propagate(e);
            }
        }
        if (!links.isEmpty()) {
            List<String> refs = links.entrySet().stream()
                    .map(l -> String.format("<a href=\"%s\"%s>%s</a>",
                            l.getValue(),
                            l.getKey().equals("AniDB") ? " target=\"_new\"" : "",
                            l.getKey()))
                    .collect(Collectors.toList());
            contentValue = contentValue + " (" + Joiner.on(" | ").join(refs) + ")";
        }
        return contentValue;
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
