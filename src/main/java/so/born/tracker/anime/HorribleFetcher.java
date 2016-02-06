package so.born.tracker.anime;

import java.io.IOException;
import java.util.List;

import so.born.tracker.cloudflare.CloudflareFetcher;

public class HorribleFetcher {
    static final String URL = "http://horriblesubs.info/lib/latest.php";

    private HorribleParser parser;
    private CloudflareFetcher fetcher;

    public HorribleFetcher(CloudflareFetcher fetcher) {
        this.fetcher = fetcher;
        this.parser = new HorribleParser();
    }

    public List<HorribleParser.Episode> feed() throws IOException {
        return parser.parse(fetcher.fetch(URL));
    }
}
