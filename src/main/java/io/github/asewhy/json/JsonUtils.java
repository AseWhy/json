package io.github.asewhy.json;

import org.jetbrains.annotations.NotNull;

class JsonUtils {
    private static final String[] REPLACEMENT_CHARS;

    /**
     * Экранировать json
     *
     * @param input входящая json строка
     * @return экранированная json строка
     */
    public static @NotNull String escapeJson(@NotNull String input) {
        var result = new StringBuilder();
        var last = 0;
        var length = input.length();

        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            var replacement = (String) null;

            if (c < 128) {
                replacement = REPLACEMENT_CHARS[c];

                if (replacement == null) {
                    continue;
                }
            } else if (c == '\u2028') {
                replacement = "\\u2028";
            } else if (c == '\u2029') {
                replacement = "\\u2029";
            } else {
                continue;
            }

            if (last < i) {
                result.append(input, last, i);
            }

            result.append(replacement);

            last = i + 1;
        }

        if (last < length) {
            result.append(input, last, length);
        }

        return result.toString();
    }

    static {
        REPLACEMENT_CHARS = new String[128];

        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }

        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
    }
}
