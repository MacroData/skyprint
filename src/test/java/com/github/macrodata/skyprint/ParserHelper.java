package com.github.macrodata.skyprint;

import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.IndexRange;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

public class ParserHelper {

    private ParserHelper() {
        super();
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

}