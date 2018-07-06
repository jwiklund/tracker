package so.born.tracker;

import java.io.IOException;
import java.util.List;
import so.born.tracker.anime.HorribleLegacyParser.Episode;

public interface HorribleFetcher {

  List<Episode> feed() throws IOException;
}
