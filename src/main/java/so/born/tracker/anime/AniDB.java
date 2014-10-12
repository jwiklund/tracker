package so.born.tracker.anime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AniDB {
    private final static Logger log = LoggerFactory.getLogger(AniDB.class);

    private SortedMap<String, Long> mapping;

    public AniDB(SortedMap<String, Long> mapping) {
        this.mapping = mapping;
    }

    public static AniDB load(Path data) throws IOException {
        SortedMap<String, Long> mapping = AniDBLoader.load(data);
        log.info("Mapping has {} entries", mapping.size());
        return new AniDB(mapping);
    }

    public List<Long> lookup(String name) {
        Set<Long> aids = new HashSet<>();
        name = name.toLowerCase();
        Long eq = null;
        for (Map.Entry<String, Long> entry : mapping.tailMap(name).entrySet()) {
            if (name.equals(entry.getKey())) {
                eq = entry.getValue();
            } else if (!entry.getKey().startsWith(name)) {
                break;
            } else if (Character.isAlphabetic(entry.getKey().charAt(name.length()))) {
                // Do not allow substrings, require word match
                aids.add(entry.getValue());
            }
        }
        if (eq != null) {
            aids.remove(eq);
        }
        List<Long> result = new ArrayList<>(aids);
        Collections.sort(result, Collections.reverseOrder());
        if (eq != null) {
            List<Long> newResult = new ArrayList<>();
            newResult.add(eq);
            newResult.addAll(result);
            result = newResult;
        }
        return result;
    }
}
