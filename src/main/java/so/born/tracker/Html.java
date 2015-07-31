package so.born.tracker;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.jersey.api.client.WebResource;

public class Html {
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537";

    public static Document fetch(WebResource resource, String url) throws IOException {
        resource.header("User-Agent", USER_AGENT);
        try (InputStream data = resource.get(InputStream.class)) {
            return Jsoup.parse(data, "utf8", url);
        }
    }
}
