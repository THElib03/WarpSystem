package eu.buildsouthwest.warp.util;

import java.text.Normalizer;
import java.util.Map;

public final class NameSanitizer {
    private NameSanitizer() {}

    private static final int MAX_LENGTH = 60;

    // letters that Unicode normalization does NOT split into base + accent
    private static final Map<Character, String> SPECIAL = Map.ofEntries(
            Map.entry('ß', "ss"), Map.entry('ø', "o"), Map.entry('Ø', "O"),
            Map.entry('æ', "ae"), Map.entry('Æ', "AE"), Map.entry('œ', "oe"),
            Map.entry('Œ', "OE"), Map.entry('đ', "d"),  Map.entry('Đ', "D"),
            Map.entry('ł', "l"),  Map.entry('Ł', "L"),  Map.entry('þ', "th")
    );

    public static String sanitize(String raw) {
        if (raw == null) return "";

        StringBuilder sb = new StringBuilder(raw.length());
        for (char c : raw.toCharArray()) {
            sb.append(SPECIAL.getOrDefault(c, String.valueOf(c)));
        }

        String s = Normalizer.normalize(sb.toString(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        s = s.replaceAll("[^A-Za-z0-9_ ]", "_");

        // 4. tidy up: collapse repeated spaces/underscores, trim, cap the length
        s = s.replaceAll(" {2,}", " ")
                .replaceAll("_{2,}", "_")
                .trim();
        if (s.length() > MAX_LENGTH) s = s.substring(0, MAX_LENGTH).trim();

        return s;
    }
}