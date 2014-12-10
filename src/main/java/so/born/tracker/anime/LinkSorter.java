package so.born.tracker.anime;

import java.util.Comparator;

public class LinkSorter implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (!o1.endsWith("p")) {
            return 1;
        }
        if (!o2.endsWith("p")) {
            return -1;
        }
        return Integer.valueOf(o1.substring(0, o1.length() - 1)) - Integer.valueOf(o2.substring(0, o2.length() - 1));
    }
}
