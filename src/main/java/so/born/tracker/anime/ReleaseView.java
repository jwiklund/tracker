package so.born.tracker.anime;

import io.dropwizard.views.View;

import java.util.List;

import com.google.common.base.Charsets;

public class ReleaseView extends View {

    private List<String> episodes;
    private String title;

    protected ReleaseView(String title, List<String> episodes) {
        super("release.ftl", Charsets.UTF_8);
        this.title = title;
        this.episodes = episodes;
    }

    public List<String> getEpisodes() {
        return episodes;
    }

    public String getTitle() {
        return title;
    }
}
