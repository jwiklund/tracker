package so.born.tracker.anime;

import com.rometools.rome.feed.synd.SyndFeed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import so.born.tracker.HorribleFetcher;
import so.born.tracker.anime.HorribleLegacyParser.Episode;

@Path("/horrible/new")
public class NewReleases {

    private HorribleFetcher fetcher;
    private AniDB anidb;

    public NewReleases(AniDB anidb, HorribleFetcher fetcher) {
        this.anidb = anidb;
        this.fetcher = fetcher;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public SyndFeed feed() throws IOException {
        ReleasesFeed feed = new ReleasesFeed(anidb, "New releases", "19610b12-0c77-48e5-871d-3045249238e5");
        for (Episode ep : fetcher.feed()) {
            if (ep.getNumber().matches("0*1")) {
                feed.addRelease(ep);
            }
        }
        return feed.toFeed();
    }

    @GET
    @Path("preview")
    @Produces(MediaType.TEXT_HTML)
    public ReleaseView preview() throws IOException {
        List<String> episodes = new ArrayList<>();
        for (Episode ep : fetcher.feed()) {
            if (ep.getNumber().matches("0*1")) {
                episodes.add(ReleasesFeed.content(anidb, ep));
            }
        }
        return new ReleaseView("New releases", episodes);
    }
}
