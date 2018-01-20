package so.born.tracker;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.lifecycle.setup.ExecutorServiceBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import java.util.concurrent.ExecutorService;
import javax.script.ScriptEngineManager;
import javax.ws.rs.client.Client;
import so.born.tracker.anime.AllReleases;
import so.born.tracker.anime.AniDB;
import so.born.tracker.anime.FollowReleases;
import so.born.tracker.anime.FollowingAnimes;
import so.born.tracker.anime.HorribleFetcher;
import so.born.tracker.anime.NewReleases;
import so.born.tracker.cloudflare.CloudflareFetcher;
import so.born.tracker.cloudflare.DDosProtectionParser;
import so.born.tracker.comic.Dilbert;
import so.born.tracker.comic.Loading;
import so.born.tracker.comic.Questionable;
import so.born.tracker.comic.Sinfest;
import so.born.tracker.comic.XKCD;
import so.born.tracker.jersey.ErrorMessageWriter;
import so.born.tracker.jersey.Ping;
import so.born.tracker.jersey.SyndFeedWriter;

public class TrackerApp extends Application<TrackerConfig> {

    @Override
    public void initialize(Bootstrap<TrackerConfig> bootstrap) {
        bootstrap.addBundle(new ViewBundle<TrackerConfig>());
    }

    @Override
    public void run(TrackerConfig config, Environment environment) throws Exception {
        JerseyClientConfiguration jerseyConfig = config.getJerseyClientConfiguration();
        jerseyConfig.setCookiesEnabled(true); // required for cloudflare ddos protection support
        final Client client = new JerseyClientBuilder(environment)
            .using(environment)
            .using(jerseyConfig)
            .build(getName());

        final ExecutorService executor = new ExecutorServiceBuilder(environment.lifecycle(), "executors")
            .build();

        ScriptEngineManager scriptManager = new ScriptEngineManager();
        CloudflareFetcher cloudFetcher = new CloudflareFetcher(client, new DDosProtectionParser(scriptManager));
        AniDB anidb = AniDB.load(config.getAnimeDBDump(), executor);
        HorribleFetcher fetcher = new HorribleFetcher(cloudFetcher);

        environment.jersey().register(new ErrorMessageWriter());
        environment.jersey().register(new SyndFeedWriter());
        environment.jersey().register(new Ping());
        environment.jersey().register(new TrackerResource());
        environment.jersey().register(new Questionable(client));
        environment.jersey().register(new XKCD(client));
        environment.jersey().register(new Sinfest(client));
        environment.jersey().register(new Dilbert(client, config.getKimonoKey()));
        environment.jersey().register(new Loading(client));
        environment.jersey().register(new AllReleases(anidb, fetcher));
        environment.jersey().register(new NewReleases(anidb, fetcher));
        environment.jersey().register(new FollowReleases(anidb, fetcher,
                new FollowingAnimes(config.getFollowedAnimes())));

        environment.healthChecks().register("config", new TrackerConfigHealh(config));
    }

    @Override
    public String getName() {
        return "tracker";
    }

    public static void main(String[] args) throws Exception {
        new TrackerApp().run(args);
    }
}
