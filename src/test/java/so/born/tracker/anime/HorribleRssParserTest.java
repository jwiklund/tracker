package so.born.tracker.anime;

import static org.junit.Assert.assertThat;

import com.google.common.base.Charsets;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;
import so.born.tracker.anime.HorribleLegacyParser.Episode;

public class HorribleRssParserTest {
  @Test
  public void parseRss() throws Exception {
    InputStream stream = getClass().getClassLoader().getResourceAsStream("horrible_rss.xml");
    SyndFeed feed = new SyndFeedInput().build(new InputStreamReader(stream, Charsets.UTF_8));
    List<Episode> episodes = new HorribleRssParser().parse(feed);

    assertThat(episodes.size(), new GreaterOrEqual<>(1));

    for (Episode episode : episodes) {
      assertThat(episode.getName(), CoreMatchers.notNullValue());
      assertThat(episode.getLink(), CoreMatchers.notNullValue());
      assertThat(episode.getLink().getName(), CoreMatchers.notNullValue());
      assertThat(episode.getLink().getLink(), CoreMatchers.notNullValue());
      assertThat(episode.getLink().getSize(), CoreMatchers.not(CoreMatchers.equalTo("missing")));
      assertThat(episode.getNumber(), CoreMatchers.notNullValue());
    }
  }
}
