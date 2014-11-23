package so.born.tracker;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import so.born.tracker.anime.FollowReleases;
import so.born.tracker.anime.FollowingAnimes;
import so.born.tracker.anime.HorribleFetcher;
import so.born.tracker.anime.NewReleases;
import so.born.tracker.comic.Questionable;
import so.born.tracker.comic.Sinfest;
import so.born.tracker.comic.XKCD;
import so.born.tracker.jersey.ErrorMessageWriter;
import so.born.tracker.jersey.Ping;
import so.born.tracker.jersey.SyndFeedWriter;

import com.sun.jersey.api.client.Client;

public class TrackerApp extends Application<TrackerConfig> {

    @Override
    public void initialize(Bootstrap<TrackerConfig> bootstrap) {
    }

    @Override
    public void run(TrackerConfig config, Environment environment) throws Exception {
        final Client client = new JerseyClientBuilder(environment)
            .using(config.getJerseyClientConfiguration())
            .build(getName());

        HorribleFetcher fetcher = new HorribleFetcher(client);

        environment.jersey().register(new ErrorMessageWriter());
        environment.jersey().register(new SyndFeedWriter());
        environment.jersey().register(new Ping());
        environment.jersey().register(new TrackerResource());
        environment.jersey().register(new Questionable(client));
        environment.jersey().register(new XKCD(client));
        environment.jersey().register(new Sinfest(client));
        environment.jersey().register(new NewReleases(fetcher));
        environment.jersey().register(new FollowReleases(fetcher,
                new FollowingAnimes(config.getFollowedAnimes())));

        environment.healthChecks().register("config", new TrackerConfigHealh());
    }

    @Override
    public String getName() {
        return "tracker";
    }

    public static void main(String[] args) throws Exception {
        new TrackerApp().run(args);
    }
}
