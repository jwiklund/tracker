package so.born.tracker.anime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class AniDBLoader {
    /**
     * <aid>|<type>|<language>|<title>
     * type:
     *   1=primary title (one per anime),
     *   2=synonyms (multiple per anime),
     *   3=shorttitles (multiple per anime),
     *   4=official title (one per language)
     */
    public static SortedMap<String, Long> load(Path data) throws IOException {
        Path titles = data.resolve("anime-titles.dat.gz");
        InputStream file = Files.newInputStream(titles, StandardOpenOption.READ);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(file), Charsets.UTF_8));
        TreeMap<String, Long> mapping = new TreeMap<String, Long>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }
            Optional<Mapping> anime = parseLine(line);
            if (anime.isPresent()) {
                mapping.put(anime.get().name.toLowerCase(), anime.get().aid);
            }
        }
        return mapping;
    }

    private final static ImmutableSet<String> LANGUAGES = ImmutableSet.of("en", "x-jat");
    private final static ImmutableSet<String> TYPES = ImmutableSet.of("1", "2", "4");

    private static class Mapping {
        public final String name;
        public final Long aid;
        public Mapping(String name, Long aid) {
            this.name = name;
            this.aid = aid;
        }
    }

    private static Optional<Mapping> parseLine(String line) {
        int idDelim = line.indexOf('|');
        int typeDelim = line.indexOf('|', idDelim + 1);
        int langDelim = line.indexOf('|', typeDelim + 1);
        Preconditions.checkArgument(idDelim != -1 && typeDelim != -1 && langDelim != -1);
        String aid = line.substring(0, idDelim);
        String type = line.substring(idDelim + 1, typeDelim);
        String lang = line.substring(typeDelim + 1, langDelim);
        if (!TYPES.contains(type)) {
            return Optional.absent();
        }
        if (!LANGUAGES.contains(lang)) {
            return Optional.absent();
        }
        String anime = line.substring(langDelim + 1);
        return Optional.of(new Mapping(anime, Long.valueOf(aid)));
    }
}
