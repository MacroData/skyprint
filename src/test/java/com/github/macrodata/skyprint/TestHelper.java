package com.github.macrodata.skyprint;

import org.codehaus.jackson.map.ObjectMapper;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.IndexRange;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.io.*;
import java.util.stream.Collectors;

final class TestHelper {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static String resource(String resource) throws IOException {
        InputStream stream = Object.class.getResourceAsStream(resource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                .collect(Collectors.joining("\n"));
        }
    }

    static String match(Rule rule, String source) {
        ParsingResult<Object> result = new ReportingParseRunner<>(rule).run(source);
        InputBuffer buffer = result.inputBuffer;
        Node<Object> node = result.parseTreeRoot;
        return buffer.extract(new IndexRange(node.getStartIndex(), node.getEndIndex()));
    }

    static ParsingResult<?> parse(Rule rule, String source) {
        return new ReportingParseRunner<>(rule).run(source);
    }

    static void debug(ParsingResult<?> result) {
        System.err.println(ParseTreeUtils.printNodeTree(result));
    }

    static String toJson(Object obj) throws IOException {
        return JSON_MAPPER.writeValueAsString(obj);
    }

    static void save(File file, String value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(value);
        }
    }

}