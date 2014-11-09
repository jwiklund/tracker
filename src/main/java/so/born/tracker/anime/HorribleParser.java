package so.born.tracker.anime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorribleParser {
    private static Logger log = LoggerFactory.getLogger(HorribleFetcher.class);
    private static final Pattern SIZE_PATTERN = Pattern.compile("\\[(\\d+p)\\]");
    private static final Pattern NAME_PATTERN = Pattern.compile("\\(\\d+/\\d+\\) (.+) - ([\\d\\.]+)");

    public List<Episode> parse(Document latest) {
        List<HorribleParser.Episode> episodes = new ArrayList<>();
        for (Element episode : latest.select("div.episode")) {
            String id = episode.attr("id");
            String nameNumber = episode.ownText();
            Matcher nameNumberMatcher = NAME_PATTERN.matcher(nameNumber);
            if (!nameNumberMatcher.matches()) {
                log.warn("Name/number did not match pattern {} for {}", nameNumber, id);
                continue;
            }
            String anime = nameNumberMatcher.group(1);
            String number = nameNumberMatcher.group(2);
            Map<String, HorribleParser.Torrent> torrents = new HashMap<>();
            for (Element torrent : episode.select("div.resolution-block")) {
                Elements nameElements = torrent.select("span.dl-label i");
                if (nameElements.size() < 1) {
                    log.trace("No \"span.dl-label i\" in {}", torrent.toString());
                    continue;
                }
                String name = nameElements.get(0).text();
                String size = "unknown";
                Matcher sizeMatcher = SIZE_PATTERN.matcher(name);
                if (sizeMatcher.find()) {
                    size = sizeMatcher.group(1);
                }
                String link = null;
                for (Element linkElement : torrent.select("span.ind-link a")) {
                    if ("Torrent".equals(linkElement.text())) {
                        link = linkElement.attr("href");
                        break;
                    }
                }
                if (link == null) {
                    log.info("No Torrent link for {}, {}", name, size);
                }
                torrents.put(size, new HorribleParser.Torrent(name, size, link));
            }
            if (torrents.isEmpty()) {
                log.warn("No Torrent links for {}", id);
                continue;
            }
            episodes.add(new HorribleParser.Episode(id, anime, number, torrents));
        }
        return episodes;
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
        public String getLink() {
            String fallback = "";
            for (Map.Entry<String, HorribleParser.Torrent> torrent : torrents.entrySet()) {
                if ("720p".equals(torrent.getKey())) {
                    return torrent.getValue().getLink();
                } else if ("480p".equals(torrent.getKey())) {
                    fallback = torrent.getValue().getLink();
                }
            }
            return fallback;
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
                    + number + ", torrents=" + torrents + "]";
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
