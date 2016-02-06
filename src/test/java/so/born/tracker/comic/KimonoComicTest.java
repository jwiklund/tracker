package so.born.tracker.comic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.junit.Test;

import com.google.common.io.Resources;
import com.rometools.rome.feed.synd.SyndFeed;

public class KimonoComicTest {
    @Test
    public void simpleVerification() throws Exception {
        Client client = mock(Client.class);
        WebTarget resource = mock(WebTarget.class);
        Invocation.Builder request = mock(Invocation.Builder.class);
        when(client.target(any(String.class))).thenReturn(resource);
        when(resource.request()).thenReturn(request);
        when(request.get(InputStream.class)).thenReturn(Resources.getResource("kimono.json").openStream());
        KimonoComic kimono = new KimonoComic(client, "", "", "title", "uuid");

        SyndFeed feed = kimono.syndFeed();

        assertEquals("title", feed.getTitle());
        assertEquals("Sunday October 18,", feed.getEntries().get(0).getTitle());
        assertEquals("http://assets.amuniversal.com/e69b66802de901330136005056a9545d", feed.getEntries().get(0).getLink());
    }
}
