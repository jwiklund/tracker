package so.born.tracker.anime;

import java.io.IOException;
import java.util.List;

import so.born.tracker.Html;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class HorribleFetcher {
    static final String URL = "http://horriblesubs.info/lib/latest.php";

    private WebResource resource;
    private HorribleParser parser;

    public HorribleFetcher(Client client) {
        resource = client.resource(URL);
        parser = new HorribleParser();
    }

    public List<HorribleParser.Episode> feed() throws IOException {
        return parser.parse(Html.fetch(resource, URL));
    }
}
