package com.projectg.geyserupdater.standalone.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigLoader {
    private final Path path;
    private final Map<String, Object> defaults;
    private final Map<String, Object> values = new HashMap<>();
    private File file;

    public ConfigLoader(Path path, Map<String, Object> defaults) {
        this.path = path;
        this.defaults = defaults;
    }

    public void load() throws IOException {
        file = new File(path.toUri());
        if (!file.exists()) {
            if (defaults == null) throw new FileNotFoundException();
            values.clear();
            values.putAll(defaults);
            save();
        }

        values.clear();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.contains("=")) throw new IOException("Invalid config line: " + line);
                int index = line.indexOf("=");
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                values.put(key, value);
            }
        }
    }

    public void save() throws IOException {
        try (FileWriter fw = new FileWriter(file, false)) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                key = key.replace("=", "-");
                fw.write(key + "=" + entry.getValue() + "\n");
            }
            fw.flush();
        }
    }

    public Object get(String key) {
        Object o = values.get(key);
        if (o == null) return defaults.get(key);
        return o;
    }

    public String getString(String key) {
        Object o = get(key);
        if (o == null) return null;
        return o.toString();
    }

    public boolean getBoolean(String key) throws NumberFormatException {
        String line = getString(key);
        if (line == null) return false;
        return Boolean.parseBoolean(line);
    }

    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
