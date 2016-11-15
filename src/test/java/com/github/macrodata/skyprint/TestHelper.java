package com.github.macrodata.skyprint;

import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.IndexRange;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
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

    public static String match(Rule rule, String source) {
        ParsingResult<Object> result = new ReportingParseRunner<>(rule).run(source);
        InputBuffer buffer = result.inputBuffer;
        Node<Object> node = result.parseTreeRoot;
        return buffer.extract(new IndexRange(node.getStartIndex(), node.getEndIndex()));
    }

    public static ParsingResult<?> parse(Rule rule, String source) {
        return new ReportingParseRunner<>(rule).run(source);
    }

    public static void debug(ParsingResult<?> result) {
        System.err.println(ParseTreeUtils.printNodeTree(result));
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