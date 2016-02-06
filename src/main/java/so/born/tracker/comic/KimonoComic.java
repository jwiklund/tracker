package so.born.tracker.comic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndFeed;

public class KimonoComic {

    private static final String URI = "https://www.kimonolabs.com/api/%s?&apikey=%s&kimmodify=1";

    private final String title;
    private final String uuid;
    private final WebTarget resource;
    private final ObjectMapper mapper = new ObjectMapper();

    public KimonoComic(Client client, String apiKey, String api, String title, String uuid) {
        this.resource = client.target(String.format(URI, api, apiKey));
        this.title = title;
        this.uuid = uuid;
    }

    public SyndFeed syndFeed() throws IOException {
        ComicFeed feed = new ComicFeed(uuid, title);

        for (KimonoComicJsonElement comic : fetch().getComics()) {
            feed.addEntry(comic.getImage(), comic.getTitle());
        }

        return feed.toFeed();
    }

    private KimonoComicJson fetch() throws IOException {
        try (InputStream data = resource.request().get(InputStream.class)) {
            return mapper.readValue(data, KimonoComicJson.class);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class KimonoComicJson {
        private List<KimonoComicJsonElement> comics;
        public List<KimonoComicJsonElement> getComics() {
            return comics;
        }
        public void setComics(List<KimonoComicJsonElement> comics) {
            this.comics = comics;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class KimonoComicJsonElement {
        private String title;
        private String image;
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getImage() {
            return image;
        }
        public void setImage(String image) {
            this.image = image;
        }
    }
}
