package so.born.tracker.cloudflare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response.Status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CloudflareFetcher {
    private Client client;
    private DDosProtectionParser parser;
    private AtomicReference<List<Cookie>> cookies = new AtomicReference<>(new ArrayList<>());

    public CloudflareFetcher(Client client, DDosProtectionParser parser) {
        this.client = client;
        this.parser = parser;
    }

    public Document fetch(String url) throws IOException {
        ClientResponse response = initialRequest(url);
        try {
            if (response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL) {
                return Jsoup.parse(response.getEntityInputStream(), "utf8", url);
            } else if (response.getStatus() == 503) {
                return handleDos(url, response);
            } else {
                throw new IOException("Fetch failed with status " + response.getStatus() + ", " + response.getStatusInfo().getReasonPhrase());
            }
        } finally {
            response.close();
        }
    }

    private ClientResponse initialRequest(String url) {
        WebResource.Builder req = client.resource(url).getRequestBuilder();
        for (Cookie cookie : cookies.get()) {
            req.cookie(cookie);
        }
        return req.get(ClientResponse.class);
    }

    private Document handleDos(String url, ClientResponse response)
            throws IOException {
        Document dos = Jsoup.parse(response.getEntityInputStream(), "utf8", url);
        ClientResponse challengeResponse = client.resource(parser.parse(dos, url))
                .header("Referer", url)
                .get(ClientResponse.class);
        try {
            if (challengeResponse.getStatus() == 200) {
                return Jsoup.parse(challengeResponse.getEntityInputStream(), "utf8", url);
            } else {
                throw new RuntimeException("Failed challenge, got status " + challengeResponse.getStatus() + ", " + challengeResponse.getStatusInfo().getReasonPhrase());
            }
        } finally {
            challengeResponse.close();
        }
    }
}
