package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.RootSection;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SkyPrintProcessor {

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
        return parseApiBlueprint(source.toCharArray());
    }

    public RootSection parseApiBlueprint(char[] source) {
        return parse(source).resultValue;
    }

    public ParsingResult<RootSection> parse(char[] source) {
        return runner.run(source);
    }

}