package so.born.tracker.comic;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import so.born.tracker.Html;

import com.rometools.rome.feed.synd.SyndFeed;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

@Path("/loading")
public class Loading {

    private static final String URL = "http://www.loadingartist.com/";
    private final WebResource resource;
    private final Client client;

    public Loading(Client client) {
        this.client = client;
        this.resource = client.resource(URL);
    }

    @GET
    public SyndFeed feed() throws IOException {
        ComicFeed feed = new ComicFeed("953687fd-05f4-4ccd-abf1-0de6051bcf4c", "Loading artist");

        Document document = Html.fetch(resource, URL);
        Elements links = document.select("#main a");
        if (links.size() > 0) {
            String current = links.get(0).attr("href");
            Document currentDocument = Html.fetch(client.resource(current), current);
            Elements currentImage = currentDocument.select(".comic img");
            String title = currentDocument.title() != null ? currentDocument.title() : "Loading artist";
            if (!currentImage.isEmpty()) {
                String currentImageUrl = currentImage.attr("src");
                if (currentImageUrl != null && currentImageUrl.startsWith("/")) {
                    currentImageUrl = URL + currentImageUrl.substring(1);
                }
                feed.addEntry(currentImageUrl, title);
            }
        }
        return feed.toFeed();
    }
}
