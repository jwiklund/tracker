package so.born.tracker.comic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import so.born.tracker.Html;

import com.google.common.base.Joiner;
import com.rometools.rome.feed.synd.SyndFeed;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

@Path("/sinfest")
@Produces(MediaType.APPLICATION_XML)
public class Sinfest {
    private static final String URL = "http://sinfest.net/";
    private WebResource resource;

    public Sinfest(Client client) {
        resource = client.resource(URL);
    }

    @GET
    public SyndFeed feed() throws UniformInterfaceException, ClientHandlerException, IOException {
        ComicFeed feed = new ComicFeed("a552af95-ac7c-4489-b8ac-f1141343f0e9", "Sinfest");

        Document document = Html.fetch(resource, URL);
        Elements comic = document.select("table img");

        boolean missing = true;
        List<String> saw = new ArrayList<>();
        for (Element element : comic) {
            String url = element.attr("src");
            if (url != null && url.matches(".*?\\d{4}-\\d{2}-\\d{2}.gif")) {
                feed.addEntry(URL + url, element.attr("alt"));
                missing = false;
                break;
            } else {
                saw.add(url);
            }
        }
        if (missing) {
            LoggerFactory.getLogger(getClass()).warn("No comic found for, saw images : {}", Joiner.on(',').join(saw));
        }
        return feed.toFeed();
    }
}
