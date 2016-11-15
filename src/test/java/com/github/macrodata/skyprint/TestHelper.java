package com.github.macrodata.skyprint;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.stream.Collectors;

final class TestHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String resource(String resource) {
        InputStream stream = Object.class.getResourceAsStream(resource);
        return new BufferedReader(new InputStreamReader(stream))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public static void save(File file, String value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(value);
        }
    }

    public static String toJsonString(Object obj) throws IOException{
        return mapper.writeValueAsString(obj);
    }

    public static String sampleReader(String pathToFile) {
        String testData = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Object.class.getResourceAsStream(pathToFile)))) {
            testData = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testData;
    }

}