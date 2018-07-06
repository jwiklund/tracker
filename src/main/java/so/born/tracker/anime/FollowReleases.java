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

@Path("/horrible/follow")
public class FollowReleases {

    private HorribleFetcher fetcher;
    private FollowingAnimes following;
    private AniDB anidb;

    public FollowReleases(AniDB anidb, HorribleFetcher fetcher, FollowingAnimes following) {
        this.anidb = anidb;
        this.fetcher = fetcher;
        this.following = following;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public SyndFeed feed() throws IOException {
        ReleasesFeed feed = new ReleasesFeed(anidb, "Following releases", "a6479a71-20c6-432a-b0f2-544ec57dfc23");
        for (Episode ep : fetcher.feed()) {
            if (following.following(ep)) {
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
            if (following.following(ep)) {
                episodes.add(ReleasesFeed.content(anidb, ep));
            }
        }
        return new ReleaseView("Following releases", episodes);
    }
}
