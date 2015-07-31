package so.born.tracker.comic;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import so.born.tracker.Html;

import com.rometools.rome.feed.synd.SyndFeed;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

@Path("/questionable")
@Produces(MediaType.APPLICATION_XML)
public class Questionable {

    private static final String URL = "http://questionablecontent.net/";
    private WebResource resource;

    public Questionable(Client client) {
        this.resource = client.resource(URL);
    }

    @GET
    public SyndFeed feed() throws UniformInterfaceException, ClientHandlerException, IOException {
        ComicFeed feed = new ComicFeed("77233e8e-5c9d-47e9-ac61-c62f1740a1b6", "Questionable Content");

        Document document = Html.fetch(resource, URL);
        Elements comic = document.select("#comic img");
        if (comic.size() == 1) {
            if (comic.size() == 1) {
                feed.addEntry(comic.get(0).attr("src"));
            } else {
                LoggerFactory.getLogger(getClass()).warn("No comic found for #comic img");
            }
        }

        return feed.toFeed();
    }

}
