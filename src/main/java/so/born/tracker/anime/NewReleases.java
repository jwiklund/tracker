package so.born.tracker.anime;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import so.born.tracker.anime.HorribleParser.Episode;

import com.rometools.rome.feed.synd.SyndFeed;

@Path("/horrible/new")
@Produces(MediaType.APPLICATION_XML)
public class NewReleases {

    private HorribleFetcher fetcher;

    public NewReleases(HorribleFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @GET
    public SyndFeed feed() throws IOException {
        ReleasesFeed feed = new ReleasesFeed("New releases", "19610b12-0c77-48e5-871d-3045249238e5");
        for (Episode ep : fetcher.feed()) {
            if (ep.getNumber().matches("0*1")) {
                feed.addRelease(ep.getName(), ep.getLink(), ep.getAltLinks());
            }
        }
        return feed.toFeed();
    }
}
