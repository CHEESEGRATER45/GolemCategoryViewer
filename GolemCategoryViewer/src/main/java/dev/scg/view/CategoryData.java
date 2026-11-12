package dev.scg.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reads the categories file: {"categories": {"Wood": ["oak_log", "*_planks", ...], ...}} */
public final class CategoryData {

    private CategoryData() {
    }

    public static Map<String, List<String>> parse(Reader reader) throws IOException {
        Map<String, List<String>> out = new LinkedHashMap<>();
        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject categories = root.getAsJsonObject("categories");
            for (Map.Entry<String, JsonElement> e : categories.entrySet()) {
                List<String> entries = new ArrayList<>();
                JsonArray array = e.getValue().getAsJsonArray();
                for (JsonElement item : array) {
                    entries.add(item.getAsString());
                }
                out.put(e.getKey(), entries);
            }
        } catch (RuntimeException e) {
            throw new IOException("Not a valid categories file: " + e.getMessage(), e);
        }
        return out;
    }
}
