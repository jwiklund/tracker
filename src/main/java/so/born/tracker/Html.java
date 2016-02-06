package so.born.tracker;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.WebTarget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Html {
    public static Document fetch(WebTarget resource, String url) throws IOException {
        try (InputStream data = resource.request().get(InputStream.class)) {
            return Jsoup.parse(data, "utf8", url);
        }
    }
}
