package so.born.tracker.anime;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import so.born.tracker.anime.HorribleParser.Episode;

import com.rometools.rome.feed.synd.SyndFeed;

@Path("/horrible/follow")
@Produces(MediaType.APPLICATION_XML)
public class FollowReleases {

    private HorribleFetcher fetcher;
    private FollowingAnimes following;

    public FollowReleases(HorribleFetcher fetcher, FollowingAnimes following) {
        this.fetcher = fetcher;
        this.following = following;
    }

    @GET
    public SyndFeed feed() throws IOException {
        ReleasesFeed feed = new ReleasesFeed("Following releases", "a6479a71-20c6-432a-b0f2-544ec57dfc23");
        for (Episode ep : fetcher.feed()) {
            if (following.following(ep)) {                
                feed.addRelease(ep.getName(), ep.getLink(), ep.getAltLinks());
            }
        }
        return feed.toFeed();
    }
}
