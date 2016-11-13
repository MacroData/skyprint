package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.*;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
class Parser extends AbstractParser {

    Rule Root() {
        return Sequence(
            push(new RootSection()),
            Optional(MetadataSection(), addAsChild()),
            Optional(OverviewSection(), addAsChild()),
            ZeroOrMore(GroupSection(), addAsChild()),
            ZeroOrMore(ResourceSection(), addAsChild())
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
        Section section = pop();
        section.setDescription(match().trim().replace("\n", " "));
        push(section);
        return true;
    }

    //************* Resource group section ****************

    Rule GroupSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            NamedSection(Sequence(GroupKeyword(), Identifier(), push(new GroupSection(match().trim())))),

            OneOrMore(
                TestNot(FirstOf(GroupNamed(), ResourceNamed())),
                FirstOf(
                    Sequence(Line(), NewLine()),
                    EmptyLine())),
            addDescription(),

            ZeroOrMore(ResourceSection(), addAsChild())
        );
    }


    //************* Resource section ****************

    Rule ResourceSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(ResourceNamed()),
            push(new ResourceSection()),
            NamedSection(FirstOf(
                Sequence(URITemplateKeyword(), addTemplate()),
                Sequence(Identifier(), addIdentifier(), Ch('['), URITemplateKeyword(), addTemplate(), Ch(']')),
                Sequence(HTTPMethodKeyword(), addMethod(), OneOrMore(Space()), URITemplateKeyword(), addTemplate()),
                Sequence(Identifier(), addIdentifier(), Ch('['), HTTPMethodKeyword(), addMethod(), OneOrMore(Space()), URITemplateKeyword(), Ch(']')))),

            OneOrMore(
                TestNot(FirstOf(ResourceNamed(), GroupNamed())),
                FirstOf(
                    Sequence(Line(), NewLine()),
                    EmptyLine())),
            addDescription()
        );
    }

    boolean addIdentifier() {
        ResourceSection section = (ResourceSection) pop();
        section.setIdentifier(match().trim());
        push(section);
        return true;
    }

    boolean addTemplate() {
        ResourceSection section = (ResourceSection) pop();
        section.setTemplate(match().trim());
        push(section);
        return true;
    }

    boolean addMethod() {
        ResourceSection section = (ResourceSection) pop();
        section.setMethod(match().trim());
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