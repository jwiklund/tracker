package so.born.tracker;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;

import so.born.tracker.comic.Questionable;
import so.born.tracker.comic.Sinfest;
import so.born.tracker.comic.XKCD;
import so.born.tracker.jersey.ErrorMessageWriter;
import so.born.tracker.jersey.SyndFeedWriter;

public class TrackerApp extends Application<TrackerConfig> {

    @Override
    public void initialize(Bootstrap<TrackerConfig> bootstrap) {
    }

    @Override
    public void run(TrackerConfig config, Environment environment) throws Exception {
        final HttpClient httpClient = new HttpClientBuilder(environment)
            .using(config.getHttpClientConfiguration())
            .build("http");
        Client client = new ApacheHttpClient4(new ApacheHttpClient4Handler(httpClient, new BasicCookieStore(), false));
        environment.jersey().register(new ErrorMessageWriter());
        environment.jersey().register(new SyndFeedWriter());
        environment.jersey().register(new TrackerResource());
        environment.jersey().register(new Questionable(client));
        environment.jersey().register(new XKCD(client));
        environment.jersey().register(new Sinfest(client));
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
