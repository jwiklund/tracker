package so.born.tracker;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrackerConfig extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    @NotNull
    @JsonProperty
    private List<String> followedAnimes = new ArrayList<>();

    public Set<String> getFollowedAnimes() {
        return new HashSet<>(followedAnimes);
    }
}
