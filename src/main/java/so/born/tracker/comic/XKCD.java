package so.born.tracker.comic;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import so.born.tracker.Html;

import com.rometools.rome.feed.synd.SyndFeed;

@Path("/xkcd")
@Produces(MediaType.APPLICATION_XML)
public class XKCD {

    private static final String URL = "http://xkcd.com/";
    private WebTarget resource;

    public XKCD(Client client) {
        resource = client.target(URL);
    }

    @GET
    public SyndFeed feed() throws IOException {
        ComicFeed feed = new ComicFeed("f5ce5c19-4793-435f-bd3d-337d3bf7ab7e", "XKCD");

        Document document = Html.fetch(resource, URL);
        Elements comic = document.select("#comic img");

        if (comic.size() == 1) {
            feed.addEntry(comic.get(0).attr("src"), comic.get(0).attr("title"));
        } else {
            LoggerFactory.getLogger(getClass()).warn("No comic found for #comic img");
        }
        return feed.toFeed();
    }
}
