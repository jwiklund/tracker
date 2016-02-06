package so.born.tracker.anime;

import java.util.Collections;
import java.util.Map;

public class DocumentEmulator {

    public String basename;
    public String answer;

    public DocumentEmulator(String basename) {
        this.basename = basename;
    }

    public ElementEmulator createElement(String type) {
        return new ElementEmulator();
    }

    public class ElementEmulator {
        public String innerHTML = "";
        public Map<String, String> firstChild = Collections.singletonMap("href", basename);
    }
}
