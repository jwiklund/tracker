package so.born.tracker.anime;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import so.born.tracker.anime.HorribleParser.Episode;
import so.born.tracker.anime.HorribleParser.Torrent;

import com.google.common.base.Strings;

public class HorribleParserTest {
    @Test
    public void testParseExample() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("horrible.html");
        Document doc = Jsoup.parse(stream, "UTF-8", HorribleFetcher.URL);
        List<Episode> episodes = new HorribleParser(new AniDB(new TreeMap<>())).parse(doc);
        assertThat(episodes, everyItem(allOf(hasInfo(), hasTorrents())));
    }

    @Test
    public void testParseAnidb() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("horrible.html");
        Document doc = Jsoup.parse(stream, "UTF-8", HorribleFetcher.URL);
        TreeMap<String, Long> mapping = new TreeMap<>();
        mapping.put("sora no method", 1l);
        List<Episode> episodes = new HorribleParser(new AniDB(mapping)).parse(doc);

        Optional<Episode> sora = episodes.stream().filter(e -> e.getName().equals("Sora no Method")).findFirst();
        assertThat(sora.isPresent(), is(true));
        assertEquals(Optional.of(1l), sora.get().getAniDBId());
    }

    private static Matcher<Episode> hasTorrents() {
        return new TypeSafeMatcher<Episode>() {
            public void describeTo(Description description) {
                description.appendText("Has torrent with info");
            }
            protected boolean matchesSafely(Episode item) {
                if (item.getTorrents().isEmpty()) {
                    return false;
                }
                for (Torrent torrent : item.getTorrents().values()) {
                    if (Strings.isNullOrEmpty(torrent.getLink())) {
                        return false;
                    }
                    if (Strings.isNullOrEmpty(torrent.getName())) {
                        return false;
                    }
                    if (Strings.isNullOrEmpty(torrent.getSize())) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static Matcher<Episode> hasInfo() {
        return new TypeSafeMatcher<Episode>() {
            public void describeTo(Description description) {
                description.appendText("Has id, name and episode");
            }
            protected boolean matchesSafely(Episode item) {
                return !Strings.isNullOrEmpty(item.getId()) &&
                        !Strings.isNullOrEmpty(item.getName()) &&
                        !Strings.isNullOrEmpty(item.getNumber());
            }
        };
    }
}
