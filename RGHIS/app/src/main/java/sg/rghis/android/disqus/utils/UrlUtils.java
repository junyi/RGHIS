package sg.rghis.android.disqus.utils;

import java.util.HashMap;
import java.util.Map;

public class UrlUtils {
    public static Map<String, String> cursor(String cursorId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("cursorId", cursorId);
        return queryParams;
    }

    public static class MapBuilder {
        private Map<String, String> map;

        public MapBuilder() {
            map = new HashMap<>();
        }

        public MapBuilder add(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Map<String, String> build() {
            return map;
        }
    }

    public static String[] author() {
        return new String[]{"author"};
    }
}
