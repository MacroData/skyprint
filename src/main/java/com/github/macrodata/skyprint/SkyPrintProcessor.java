package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.RootSection;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SkyPrintProcessor {

    private static final ObjectMapper JSON_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        JSON_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        JSON_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    private final Parser parser;
    private final ParseRunner<RootSection> runner;

    public SkyPrintProcessor() {
        this(Parboiled.createParser(Parser.class));
    }

    public SkyPrintProcessor(Parser parser) {
        this(parser, new ReportingParseRunner<>(parser.Root()));
    }

    public SkyPrintProcessor(Parser parser, ParseRunner<RootSection> runner) {
        this.parser = parser;
        this.runner = runner;
    }

    public RootSection parseApiBlueprint(InputStream stream) {
        return parseApiBlueprint(new BufferedReader(new InputStreamReader(stream)));
    }

    public RootSection parseApiBlueprint(BufferedReader reader) {
        return parseApiBlueprint(reader.lines().collect(Collectors.joining("\n")));
    }

    public RootSection parseApiBlueprint(String source) {
        return parseApiBlueprint((source + "\n").toCharArray());    //TODO: fix bug with incorrect read last empty line
    }

    public RootSection parseApiBlueprint(char[] source) {
        return parse(source).resultValue;
    }

    public ParsingResult<RootSection> parse(char[] source) {
        return runner.run(source);
    }

    public String parseToJson(String resource) throws IOException {
        return apiBlueprintToJson(parseApiBlueprint(resource));
    }

    public static String apiBlueprintToJson(RootSection root) throws IOException {
        return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

}