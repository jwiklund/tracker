package so.born.tracker.comic;

import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.Resources;
import com.rometools.rome.feed.synd.SyndFeed;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KimonoComicTest {
    @Test
    public void simpleVerification() throws Exception {
        Client client = mock(Client.class);
        WebResource resource = mock(WebResource.class);
        when(client.resource(any(String.class))).thenReturn(resource);
        when(resource.get(InputStream.class)).thenReturn(Resources.getResource("kimono.json").openStream());
        KimonoComic kimono = new KimonoComic(client, "", "", "title", "uuid");

        SyndFeed feed = kimono.syndFeed();

        assertEquals("title", feed.getTitle());
        assertEquals("Sunday October 18,", feed.getEntries().get(0).getTitle());
        assertEquals("http://dilbert.com/strip/2015-10-18", feed.getEntries().get(0).getLink());
    }
}
