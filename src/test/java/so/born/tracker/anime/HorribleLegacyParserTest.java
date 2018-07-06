package so.born.tracker.anime;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Strings;
import java.io.InputStream;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import so.born.tracker.anime.HorribleLegacyParser.Episode;
import so.born.tracker.anime.HorribleLegacyParser.Torrent;

public class HorribleLegacyParserTest {
    @Test
    public void testParseExample() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("horrible.html");
        Document doc = Jsoup.parse(stream, "UTF-8", HorribleLegacyFetcher.URL);
        List<Episode> episodes = new HorribleLegacyParser().parse(doc);
        assertTrue("" + episodes.size(), episodes.size() > 0);
        assertThat(episodes, everyItem(allOf(hasInfo(), hasTorrents())));
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
