package so.born.tracker.comic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
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
        resource.get(InputStream.class);
        Document document = Jsoup.parse(resource.get(InputStream.class), "utf8", URL);
        Element comic = document.getElementById("comic");
        String url = null;
        if (comic != null) {
            for (Element img : comic.select("img")) {
                url = img.attr("src");
            }
        }
        
        List<SyndEntry> entries = new ArrayList<>(1);
        if (url != null) {
            SyndEntryImpl entry = new SyndEntryImpl();
            entry.setTitle("Comic of the Day");
            entry.setLink(url);
            SyndContentImpl content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue("<img src=" + url + ">");
            entry.getContents().add(content);
            entries.add(entry);
        }

        SyndFeed sf = new SyndFeedImpl();
        sf.setFeedType("rss_2.0");
        sf.setTitle("Questionable Content");
        sf.setDescription("Questionable Content");
        sf.setLink("77233e8e-5c9d-47e9-ac61-c62f1740a1b6");
        sf.setEntries(entries);
        return sf;
    }

}
