package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.Section;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
public class Parser extends BaseParser<Section> {

    Rule Root() {
        return OneOrMore(ANY);
    }

}
