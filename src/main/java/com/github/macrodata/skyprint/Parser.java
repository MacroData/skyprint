package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.MetadataSection;
import com.github.macrodata.skyprint.section.RootSection;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;

@BuildParseTree
class Parser extends AbstractParser {

    Rule Root() {
        return Sequence(
            push(new RootSection()),
            MetadataSection(), addAsChild(),
            OneOrMore(Any())
        );
    }

    //************* MetaData Section ****************

    Rule MetadataSection() {
        return Sequence(
            push(new MetadataSection()),
            Pair(String("FORMAT"), Line()), addPair(),
            ZeroOrMore(TestNot(EmptyLine()), Pair(Key(), Line()), addPair()));
    }

    Rule Pair(Rule key, Rule value) {
        return Sequence(key, Ch(':'), value, NewLine());
    }

    @SuppressSubnodes
    Rule Key() {
        return OneOrMore(TestNot(Ch(':')), Any());
    }

    boolean addPair() {
        MetadataSection metadata = (MetadataSection) pop();
        String[] match = match().split(":", 2);
        metadata.put(match[0].trim(), match[1].trim());
        push(metadata);
        return true;
    }

}