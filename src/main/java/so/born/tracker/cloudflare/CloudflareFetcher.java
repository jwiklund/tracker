package so.born.tracker.cloudflare;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CloudflareFetcher {
    private Client client;
    private DDosProtectionParser parser;

    public CloudflareFetcher(Client client, DDosProtectionParser parser) {
        this.client = client;
        this.parser = parser;
    }

    public Document fetch(String url) throws IOException {
        Response response = initialRequest(url);
        try {
            if (response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL) {
                Document parsed = Jsoup.parse((InputStream) response.getEntity(), "utf8", url);
                System.out.println(parsed);
                return parsed;
            } else if (response.getStatus() == 503) {
                return handleDos(url, response);
            } else {
                throw new IOException("Fetch failed with status " + response.getStatus() + ", " + response.getStatusInfo().getReasonPhrase());
            }
        } finally {
            response.close();
        }
    }

    private Response initialRequest(String url) {
        return client.target(url).request().get();
    }

    private Document handleDos(String url, Response response)
            throws IOException {
        Document dos = Jsoup.parse((InputStream) response.getEntity(), "utf8", url);
        Response challengeResponse = client.target(parser.parse(dos, url))
                .request()
                .header("Referer", url)
                .get();
        try {
            if (challengeResponse.getStatus() == 200) {
                return Jsoup.parse((InputStream) challengeResponse.getEntity(), "utf8", url);
            } else {
                throw new RuntimeException("Failed challenge, got status " + challengeResponse.getStatus() + ", " + challengeResponse.getStatusInfo().getReasonPhrase());
            }
        } finally {
            challengeResponse.close();
        }
    }
}
