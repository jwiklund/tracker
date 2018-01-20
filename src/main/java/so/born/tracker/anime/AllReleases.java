package so.born.tracker.anime;

import com.rometools.rome.feed.synd.SyndFeed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import so.born.tracker.anime.HorribleParser.Episode;

@Path("/horrible/all")
public class AllReleases {

    private AniDB anidb;
    private HorribleFetcher fetcher;

    public AllReleases(AniDB anidb, HorribleFetcher fetcher) {
        this.anidb = anidb;
        this.fetcher = fetcher;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public SyndFeed feed() throws IOException {
        ReleasesFeed feed = new ReleasesFeed(anidb, "Following releases", "a6479a71-20c6-432a-b0f2-544ec57dfc23");
        for (Episode ep : fetcher.feed()) {
            feed.addRelease(ep);
        }
        return feed.toFeed();
    }

    @GET
    @Path("preview")
    @Produces(MediaType.TEXT_HTML)
    public ReleaseView preview() throws IOException {
        List<String> episodes = new ArrayList<>();
        for (Episode ep : fetcher.feed()) {
            episodes.add(ReleasesFeed.content(anidb, ep));
        }
        return new ReleaseView("Following releases", episodes);
    }
}
