package so.born.tracker.anime;

import java.io.IOException;
import java.util.List;
import so.born.tracker.HorribleFetcher;
import so.born.tracker.cloudflare.CloudflareFetcher;

public class HorribleLegacyFetcher implements HorribleFetcher {
    static final String URL = "https://horriblesubs.info/api.php?method=getlatest";

    private HorribleLegacyParser parser;
    private CloudflareFetcher fetcher;

    public HorribleLegacyFetcher(CloudflareFetcher fetcher) {
        this.fetcher = fetcher;
        this.parser = new HorribleLegacyParser();
    }

    @Override
    public List<HorribleLegacyParser.Episode> feed() throws IOException {
        return parser.parse(fetcher.fetch(URL));
    }
}
