package com.github.macrodata.skyprint;

import java.io.*;
import java.util.stream.Collectors;

final class TestHelper {

    public static String resource(String resource) {
        InputStream stream = Object.class.getResourceAsStream(resource);
        if (stream == null) return "";
        return new BufferedReader(new InputStreamReader(stream))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public static void save(File file, String value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(value);
        }
    }

}