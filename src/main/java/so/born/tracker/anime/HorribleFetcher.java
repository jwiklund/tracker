package so.born.tracker.anime;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class HorribleFetcher {
    static final String URL = "http://horriblesubs.info/lib/latest.php";

    private WebResource resource;
    private HorribleParser parser;

    public HorribleFetcher(Client client, AniDB anidb) {
        resource = client.resource(URL);
        parser = new HorribleParser(anidb);
    }

    public List<HorribleParser.Episode> feed() throws IOException {
        InputStream data = resource.get(InputStream.class);
        Document latest = Jsoup.parse(data, "UTF-8", URL);
        return parser.parse(latest);
    }
}
