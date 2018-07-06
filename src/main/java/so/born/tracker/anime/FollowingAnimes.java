package so.born.tracker.anime;

import java.util.Set;

import so.born.tracker.anime.HorribleLegacyParser.Episode;

public class FollowingAnimes {

    private Set<String> following;
    public FollowingAnimes(Set<String> following) {
        this.following = following;        
    }

    public boolean following(Episode ep) {
        return following.contains(ep.getName());
    }
}
