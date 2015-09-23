package sg.rghis.android.disqus.utils;

import java.util.HashMap;
import java.util.Map;

public class UrlUtils {
    public static Map<String, String> cursor(String cursorId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("cursorId", cursorId);
        return queryParams;
    }

    public static String[] author() {
        return new String[]{"author"};
    }
}
