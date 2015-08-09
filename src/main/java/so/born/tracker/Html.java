package so.born.tracker;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.jersey.api.client.WebResource;

public class Html {
    public static Document fetch(WebResource resource, String url) throws IOException {
        try (InputStream data = resource.get(InputStream.class)) {
            return Jsoup.parse(data, "utf8", url);
        }
    }
}
