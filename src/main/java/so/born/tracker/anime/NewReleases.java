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
        NewReleasesFeed feed = new NewReleasesFeed();
        for (Episode ep : fetcher.feed()) {
            feed.addRelease(ep.getName());
        }
        return feed.toFeed();
    }
}
