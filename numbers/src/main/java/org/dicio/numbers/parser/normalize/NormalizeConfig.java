package org.dicio.numbers.parser.normalize;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalizeConfig {

    private static final Map<String, String> DEFAULT_ACCENTS = new HashMap<String, String>() {{
        put("á", "a");
        put("à", "a");
        put("ã", "a");
        put("â", "a");
        put("é", "e");
        put("è", "e");
        put("ê", "e");
        put("ẽ", "e");
        put("í", "i");
        put("ì", "i");
        put("î", "i");
        put("ĩ", "i");
        put("ò", "o");
        put("ó", "o");
        put("ô", "o");
        put("õ", "o");
        put("ú", "u");
        put("ù", "u");
        put("û", "u");
        put("ũ", "u");
        put("Á", "A");
        put("À", "A");
        put("Ã", "A");
        put("Â", "A");
        put("É", "E");
        put("È", "E");
        put("Ê", "E");
        put("Ẽ", "E");
        put("Í", "I");
        put("Ì", "I");
        put("Î", "I");
        put("Ĩ", "I");
        put("Ò", "O");
        put("Ó", "O");
        put("Ô", "O");
        put("Õ", "O");
        put("Ú", "U");
        put("Ù", "U");
        put("Û", "U");
        put("Ũ", "U");
    }};

    private static final List<String> DEFAULT_SYMBOLS = Arrays.asList(";", "_", "!", "?", "<", ">",
            "|", "(", ")", "=", "[", "]", "{", "}", "»", "«", "*", "~", "^", "`");

    public static final NormalizeConfig DEFAULT_CONFIG = new NormalizeConfig();


    public final boolean shouldLowercase;
    public final boolean shouldNumberToDigits;
    public final boolean shouldExpandContractions;
    public final boolean shouldRemoveSymbols;
    public final boolean shouldRemoveAccents;
    public final boolean shouldRemoveArticles;
    public final boolean shouldRemoveStopWords;

    public final Map<String, String> contractions;
    public final Map<String, String> wordReplacements;
    public final Map<String, String> numberReplacements;
    public final Map<String, String> accents;

    public final List<String> stopWords;
    public final List<String> articles;
    public final List<String> symbols;

    private NormalizeConfig() {
        // default values for default config

        shouldLowercase = false;
        shouldNumberToDigits = true;
        shouldExpandContractions = true;
        shouldRemoveSymbols = false;
        shouldRemoveAccents = false;
        shouldRemoveArticles = false;
        shouldRemoveStopWords = false;

        contractions = Collections.emptyMap();
        wordReplacements = Collections.emptyMap();
        numberReplacements = Collections.emptyMap();
        accents = DEFAULT_ACCENTS;

        stopWords = Collections.emptyList();
        articles = Collections.emptyList();
        symbols = DEFAULT_SYMBOLS;
    }

    public NormalizeConfig(final String configFolder) {
        try {
            final JsonObject root = JsonParser.object().from(ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(configFolder + "/normalize.json"));

            shouldLowercase = root.getBoolean("lowercase", false);
            shouldNumberToDigits = root.getBoolean("numbers_to_digits", true);
            shouldExpandContractions = root.getBoolean("expand_contractions", true);
            shouldRemoveSymbols = root.getBoolean("remove_symbols", false);
            shouldRemoveAccents = root.getBoolean("remove_accents", false);
            shouldRemoveArticles = root.getBoolean("remove_articles", false);
            shouldRemoveStopWords = root.getBoolean("remove_stopwords", false);

            contractions = getStringMap(root, "contractions", Collections.emptyMap());
            wordReplacements = getStringMap(root, "word_replacements", Collections.emptyMap());
            numberReplacements = getStringMap(root, "number_replacements", Collections.emptyMap());
            accents = getStringMap(root, "accents", DEFAULT_ACCENTS);

            stopWords = getStringList(root, "stopwords", Collections.emptyList());
            articles = getStringList(root, "articles", Collections.emptyList());
            symbols = getStringList(root, "symbols", DEFAULT_SYMBOLS);

        } catch (final JsonParserException e) {
            throw new RuntimeException(e);
        }
    }


    private static Map<String, String> getStringMap(final JsonObject root,
                                                    final String key,
                                                    final Map<String, String> defaultMap) {
        if (!(root.get(key) instanceof JsonObject)) {
            return defaultMap;
        }

        final Map<String, String> result = new HashMap<>();
        for (final Map.Entry<String, Object> entry : root.getObject(key).entrySet()) {
            if (entry.getValue() instanceof String) {
                result.put(entry.getKey(), (String) entry.getValue());
            } else {
                throw new IllegalArgumentException(
                        "Invalid value type " + entry.getValue() + " for map " + key);
            }
        }

        return result;
    }

    private static List<String> getStringList(final JsonObject root,
                                              final String key,
                                              final List<String> defaultList) {
        if (!(root.get(key) instanceof JsonArray)) {
            return defaultList;
        }

        final List<String> result = new ArrayList<>();
        for (final Object object : root.getArray(key)) {
            if (object instanceof String) {
                result.add((String) object);
            } else {
                throw new IllegalArgumentException(
                        "Invalid value type " + object + " for array " + key);
            }
        }

        return result;
    }
}
