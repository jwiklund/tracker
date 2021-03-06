package so.born.tracker.anime;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class AniDB {
    private final static Logger log = LoggerFactory.getLogger(AniDB.class);

    private CompletableFuture<SortedMap<String, Long>> mapping;

    public AniDB(SortedMap<String, Long> mapping) {
        this.mapping = CompletableFuture.completedFuture(mapping);
    }

    public AniDB(CompletableFuture<SortedMap<String, Long>> mapping) {
        this.mapping = mapping;
    }

    public static AniDB load(String data, ExecutorService executor) throws IOException {
        CompletableFuture<SortedMap<String, Long>> mapping = CompletableFuture.supplyAsync(() -> {
            SortedMap<String, Long> load;
            try {
                load = AniDBLoader.load(FileSystems.getDefault().getPath(data));
            } catch (IOException e) {
                log.warn("Load mapping failed", e);
                return new TreeMap<>();
            }
            log.info("Mapping has {} entries", load.size());
            return load;
        }, executor);
        return new AniDB(mapping);
    }

    public Optional<Long> lookupFirst(String name) {
        List<Long> matches = lookup(name);
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(matches.get(0));
    }

    public List<Long> lookup(String name) {
        Set<Long> aids = new HashSet<>();
        name = normalizeName(name);
        Long eq = null;
        for (Map.Entry<String, Long> entry : getMapping().tailMap(name).entrySet()) {
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

    private SortedMap<String, Long> getMapping() {
        try {
            return mapping.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static String normalizeName(String name) {
        String stripped = name.codePoints()
            .filter(cp -> Character.isLetterOrDigit(cp) || Character.isWhitespace(cp))
            .map(i -> Character.toLowerCase(i))
            .collect(() -> new StringBuilder(),
                    (byteBuffer, charCode) -> byteBuffer.append(Character.toChars(charCode)),
                    (charCode1, charCode2) -> charCode1.append(charCode2))
            .toString();
        if (!stripped.isEmpty()) {
            return Joiner.on(" ").join(stripped.split(" +"));
        }
        return name.toLowerCase();
    }
}
