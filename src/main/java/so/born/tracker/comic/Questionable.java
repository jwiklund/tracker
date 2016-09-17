package so.born.tracker.comic;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rometools.rome.feed.synd.SyndFeed;

import so.born.tracker.Html;

@Path("/questionable")
@Produces(MediaType.APPLICATION_XML)
public class Questionable {

    private static final String URL = "http://questionablecontent.net/";
    private WebTarget resource;

    public Questionable(Client client) {
        this.resource = client.target(URL);
    }

    @GET
    public SyndFeed feed() throws IOException {
        ComicFeed feed = new ComicFeed("77233e8e-5c9d-47e9-ac61-c62f1740a1b6", "Questionable Content");

        Document document = Html.fetch(resource, URL);
        Elements comic = document.select("img");
        for (Element img : comic) {
            String source = img.attr("src");
            if (source != null && source.startsWith("http://www.questionablecontent.net/comics/")) {
                feed.addEntry(source);
            }
        }

        return feed.toFeed();
    }

}
