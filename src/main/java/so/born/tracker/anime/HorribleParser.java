package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class HorribleParser {
    private static Logger log = LoggerFactory.getLogger(HorribleFetcher.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("(.+) - ([\\d\\.]+) \\[([^]]+)\\]");

    public HorribleParser() {
    }

    public List<Episode> parse(Document latest) {
        Multimap<NameNumber, Torrent> episodes = ArrayListMultimap.create();
        for (Element torrent : latest.select("div.release-links")) {
            String id = torrentId(torrent.attr("class"));
            String nameNumber = getText(torrent.select("td.dl-label i"));
            Matcher nameNumberMatcher = NAME_PATTERN.matcher(nameNumber);
            if (!nameNumberMatcher.matches()) {
                log.warn("Name/number did not match pattern {} for {}", nameNumber, id);
                continue;
            }
            String anime = nameNumberMatcher.group(1);
            String number = nameNumberMatcher.group(2);
            String size = nameNumberMatcher.group(3);
            String torrentLink = getLink(torrent.select("td.hs-torrent-link a"));
            if (torrentLink.isEmpty()) {
                log.warn("Torrent link not found {} for {}", nameNumber, id);
                continue;
            }
            episodes.put(new NameNumber(anime, number),
                    new HorribleParser.Torrent(anime + " - " + number, size, torrentLink));
        }

        List<Episode> result = new ArrayList<HorribleParser.Episode>();
        for (Map.Entry<NameNumber, Collection<HorribleParser.Torrent>> episode : episodes.asMap().entrySet()) {
            Map<String, Torrent> torrents = episode.getValue().stream()
                    .collect(Collectors.toMap(
                            torrent -> torrent.getSize(),
                            torrent -> torrent));
            result.add(new Episode(episode.getKey().name + "-" + episode.getKey().number,
                    episode.getKey().name,
                    episode.getKey().number,
                    torrents));
        }
        return result;
    }

    private String getLink(Elements element) {
        Iterator<Element> elements = element.iterator();
        if (elements.hasNext()) {
            return elements.next().attr("href");
        }
        return "";
    }

    private String getText(Elements element) {
        Iterator<Element> elements = element.iterator();
        if (elements.hasNext()) {
            return elements.next().ownText();
        }
        return "";
    }

    private String torrentId(String classAttr) {
        List<String> ids = Arrays.asList(classAttr.split(" ")).stream()
            .filter(item -> !"release-links".equals(item))
            .collect(Collectors.toList());
        if (ids.size() > 0) {
            return ids.get(0);
        }
        return "";
    }

    private static class NameNumber {
        public final String name;
        public final String number;

        public NameNumber(String name, String number) {
            this.name = name;
            this.number = number;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result
                    + ((number == null) ? 0 : number.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NameNumber other = (NameNumber) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (number == null) {
                if (other.number != null)
                    return false;
            } else if (!number.equals(other.number))
                return false;
            return true;
        }
    }

    public static class Episode {
        private String id;
        private String name;
        private String number;
        private Map<String, HorribleParser.Torrent> torrents;
        public Episode(String id, String name, String number, Map<String, HorribleParser.Torrent> torrents) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.torrents = torrents;
        }
        public String getNumber() {
            return number;
        }
        public void setNumber(String number) {
            this.number = number;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Map<String, HorribleParser.Torrent> getTorrents() {
            return torrents;
        }
        public void setTorrents(Map<String, HorribleParser.Torrent> torrents) {
            this.torrents = torrents;
        }
        public Torrent getLink() {
            Torrent fallback = new Torrent("", "", "");
            for (Map.Entry<String, HorribleParser.Torrent> torrent : torrents.entrySet()) {
                if ("720p".equals(torrent.getKey())) {
                    return torrent.getValue();
                } else if ("480p".equals(torrent.getKey())) {
                    fallback = torrent.getValue();
                }
            }
            return fallback;
        }
        public Map<String, Torrent> getAltLinks() {
            Map<String, Torrent> alts = new LinkedHashMap<>();
            Torrent main = getLink();
            for (Map.Entry<String, HorribleParser.Torrent> torrent : torrents.entrySet()) {
                if (torrent.getValue() != main) {
                    alts.put(torrent.getKey(), torrent.getValue());
                }
            }
            return alts;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result
                    + ((number == null) ? 0 : number.hashCode());
            result = prime * result
                    + ((torrents == null) ? 0 : torrents.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Episode other = (Episode) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (number == null) {
                if (other.number != null)
                    return false;
            } else if (!number.equals(other.number))
                return false;
            if (torrents == null) {
                if (other.torrents != null)
                    return false;
            } else if (!torrents.equals(other.torrents))
                return false;
            return true;
        }
        @Override
        public String toString() {
            return "Episode [id=" + id + ", name=" + name + ", number="
                    + number + ", torrents="
                    + torrents + "]";
        }
    }

    public static class Torrent {
        private String name;
        private String size;
        private String link;
        
        public Torrent(String name, String size, String link) {
            this.name = name;
            this.size = size;
            this.link = link;
        }
        @Override
        public String toString() {
            return "Torrent [name=" + name + ", size=" + size + ", link="
                    + link + "]";
        }
        public String getSize() {
            return size;
        }
        public void setSize(String size) {
            this.size = size;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getLink() {
            return link;
        }
        public void setLink(String link) {
            this.link = link;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((link == null) ? 0 : link.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((size == null) ? 0 : size.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Torrent other = (Torrent) obj;
            if (link == null) {
                if (other.link != null)
                    return false;
            } else if (!link.equals(other.link))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (size == null) {
                if (other.size != null)
                    return false;
            } else if (!size.equals(other.size))
                return false;
            return true;
        }
        public Torrent(String name, String link) {
            super();
            this.name = name;
            this.link = link;
        }
    }
}
