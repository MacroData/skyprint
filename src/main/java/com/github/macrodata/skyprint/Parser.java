package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.MetadataSection;
import com.github.macrodata.skyprint.section.OverviewSection;
import com.github.macrodata.skyprint.section.RootSection;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
class Parser extends AbstractParser {

    Rule Root() {
        return Sequence(
            push(new RootSection()),
            Optional(MetadataSection(), addAsChild()),
            Optional(OverviewSection(), addAsChild()),
            ZeroOrMore(Any())
        );
    }

    //************* Abstract Section ****************

    Rule NamedSection(Rule body) {
        return Sequence(
            FirstOf("######", "#####", "####", "###", "##", "#"),
            OneOrMore(Space()),
            body,
            NewLine());
    }

    Rule ResourceNamed() {
        return NamedSection(FirstOf(
            URITemplateKeyword(),
            Sequence(Identifier(), Ch('['), URITemplateKeyword(), Ch(']')),
            Sequence(HTTPMethodKeyword(), OneOrMore(Space()), URITemplateKeyword()),
            Sequence(Identifier(), Ch('['), HTTPMethodKeyword(), OneOrMore(Space()), URITemplateKeyword(), Ch(']'))));
    }

    Rule GroupNamed() {
        return NamedSection(Sequence(GroupKeyword(), Identifier()));
    }

    Rule Identifier() {
        return OneOrMore(TestNot(FirstOf(AnyOf("[]()"), NewLine())), Any());
    }

    //************* MetaData Section ****************

    Rule MetadataSection() {
        return Sequence(
            push(new MetadataSection()),

            Pair(String("FORMAT"), Line()), addPair(),

            ZeroOrMore(
                TestNot(EmptyLine()),
                Pair(Key(), Line()), addPair()));
    }

    Rule Pair(Rule key, Rule value) {
        return Sequence(key, Ch(':'), value, NewLine());
    }

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

    //************* API name & Overview Section ****************

    Rule OverviewSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            NamedSection(Sequence(Line(), push(new OverviewSection(match().trim())))),

            OneOrMore(
                TestNotKeyword(),
                FirstOf(
                    Sequence(Line(), NewLine()),
                    EmptyLine())),
            addDescription(),
            true
        );
    }

    boolean addDescription() {
        OverviewSection section = (OverviewSection) pop();
        section.setDescription(match().trim().replace("\n", " "));
        push(section);
        return true;
    }

    //************* Keywords ****************

    Rule TestNotKeyword() {
        return TestNot(FirstOf(
            GroupNamed(),
            ResourceNamed()));
    }

    Rule GroupKeyword() {
        return String("Group");
    }

    Rule DataStructuresKeyword() {
        return String("Data Structures");
    }

    Rule HTTPMethodKeyword() {
        return FirstOf("CONNECT", "DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "TRACE");
    }

    Rule URITemplateKeyword() {
        return Sequence(
            Ch('/'),
            OneOrMore(FirstOf(Letter(), Digit(), AnyOf("_%."), MultipleVariables())),
            ZeroOrMore(URITemplateKeyword()));
    }

    Rule MultipleVariables() {
        return Sequence(
            Ch('{'),
            Optional(AnyOf("#+?&")),
            OneOrMore(FirstOf(Letter(), Digit(), AnyOf("_%,"))),
            Ch('}')
        );
    }

}