package so.born.tracker.comic;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;

import com.rometools.rome.feed.synd.SyndFeed;

@Path("/dilbert")
public class Dilbert {

    private KimonoComic kimono;

    public Dilbert(Client client, String apiKey) {
        kimono = new KimonoComic(client, apiKey, "bf4pusxy", "Dilbert", "9d0a8c0d-1312-40c9-ba15-f9b14895c400");
    }

    @GET
    public SyndFeed syndFeed() throws IOException {
        return kimono.syndFeed();
    }
}
