package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.*;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BuildParseTree
class Parser extends AbstractParser {

    Rule Root() {
        return Sequence(
            push(new RootSection()),
            Optional(MetadataSection(), addAsChild()),
            Optional(OverviewSection(), addAsChild()),
            ZeroOrMore(ResourceSection(), addAsChild()),
            ZeroOrMore(GroupSection(), addAsChild())
        );
    }

    //************* Abstract Section ****************

    Rule NamedSection(Rule body) {
        return Sequence(
            FirstOf(
                FirstOf("######", "#####", "####", "###", "##", "#"),
                FirstOf("++++++", "+++++", "++++", "+++", "++", "+")),
            OneOrMore(Space()),
            body,
            NewLine());
    }

    Rule PayloadSection(Rule body) {
        return Sequence(

            body,
            NewLine()
        );
    }

    Rule ResourceNamed() {
        return NamedSection(FirstOf(
            URITemplateKeyword(),
            Sequence(Identifier(), Ch('['), URITemplateKeyword(), Ch(']')),
            Sequence(HTTPMethodKeyword(), OneOrMore(Space()), URITemplateKeyword()),
            Sequence(Identifier(), Ch('['), HTTPMethodKeyword(), OneOrMore(Space()), URITemplateKeyword(), Ch(']'))));
    }

    Rule ActionNamed() {
        return NamedSection(FirstOf(
            HTTPMethodKeyword(),
            Sequence(Identifier(), Ch('['), HTTPMethodKeyword(), Ch(']')),
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
        Map<String, String> metadata = (Map<String, String>) pop();
        String[] match = match().split(":", 2);
        metadata.put(match[0].trim(), match[1].trim());
        push((Section) metadata);
        return true;
    }

    //************* API name & Overview Section ****************

    Rule OverviewSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(NamedSection(Line())),
            push(new OverviewSection()),
            NamedSection(Sequence(Line(), addNameO())),

            OneOrMore(
                TestNotKeyword(),
                Any()),
            addDescription(),
            true
        );
    }

    boolean addNameO() {
        OverviewSection section = (OverviewSection) peek();
        section.setName(match().trim());
        return true;
    }

    boolean addDescription() {
        Section section = (Section) pop();
        section.setDescription(match().trim().replace("\n", " "));
        push(section);
        return true;
    }

    //************* Resource group section ****************

    Rule GroupSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(NamedSection(Sequence(GroupKeyword(), Identifier()))),
            push(new GroupSection()),
            NamedSection(Sequence(GroupKeyword(), Identifier(), addIdentifierGroup())),

            OneOrMore(
                TestNot(FirstOf(GroupNamed(), ResourceNamed())),
                Any()),
            addDescription(),

            ZeroOrMore(ResourceSection(), addAsChild())
        );
    }

    boolean addIdentifierGroup() {
        GroupSection section = (GroupSection) peek();
        section.setIdentifier(match().trim());
        return true;
    }

    //************* Resource section ****************

    Rule ResourceSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(ResourceNamed()),
            push(new ResourceSection()),
            NamedSection(FirstOf(
                Sequence(URITemplateKeyword(), addTemplate()),
                Sequence(HTTPMethodKeyword(), addMethod(), OneOrMore(Space()), URITemplateKeyword(), addTemplate()),
                Sequence(Identifier(), addIdentifier(), Ch('['), URITemplateKeyword(), addTemplate(), Ch(']')),
                Sequence(Identifier(), addIdentifier(), Ch('['), HTTPMethodKeyword(), addMethod(), OneOrMore(Space()), URITemplateKeyword(), Ch(']')))),

            OneOrMore(
                TestNot(FirstOf(ResourceNamed(), GroupNamed(), ActionNamed())),
                Any()),
            addDescription(),

            ZeroOrMore(
                Sequence(ActionSection(), addAsChild()))
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

    //************* Action section ****************

    Rule ActionSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(ActionNamed()),
            push(new ActionSection()),
            NamedSection(FirstOf(
                Sequence(HTTPMethodKeyword(), addMethodA()),
                Sequence(Identifier(), addIdentifierA(), Ch('['), HTTPMethodKeyword(), addMethodA(), Ch(']')),
                Sequence(Identifier(), Ch('['), HTTPMethodKeyword(), addMethodA(), OneOrMore(Space()), URITemplateKeyword(), addTemplateA(), Ch(']')))),

            OneOrMore(
                TestNot(FirstOf(ActionNamed(), ResourceNamed(), GroupNamed())),
                Any()),
            addDescription()
        );
    }

    boolean addIdentifierA() {
        ActionSection section = (ActionSection) pop();
        section.setIdentifier(match().trim());
        push(section);
        return true;
    }

    boolean addTemplateA() {
        ActionSection section = (ActionSection) pop();
        section.setTemplate(match().trim());
        push(section);
        return true;
    }

    boolean addMethodA() {
        ActionSection section = (ActionSection) pop();
        section.setMethod(match().trim());
        push(section);
        return true;
    }

    //************* Response Section ****************

    Rule ResponseSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            null
        );
    }

    Rule ResponseNamed() {
        return PayloadSection(FirstOf(
            HTTPMethodKeyword(),
            null
        ));
    }

    //************* Headers Section ****************

    Rule HeadersNamed() {
        return NamedSection(String("Headers"));
    }

    Rule HeadersSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(HeadersNamed()), push(new HeadersSection()), HeadersNamed(),
            EmptyLine(),

            ZeroOrMore(
                TestNot(EmptyLine()),
                Pair(Key(), Line()), addPair())
        );
    }

    //************* Attributes Section ****************

    Rule AttributesNamed() {
        return NamedSection(Sequence(
            String("Attributes"), Optional(OneOrMore(Space()), Ch('('), Identifier(), Ch(')'))));
    }

    Rule AttributesSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(AttributesNamed()), push(new AttributesSection()),

            NamedSection(Sequence(
                String("Attributes"),
                Optional(OneOrMore(Space()), Ch('('), Identifier(), addTypeDefinition(), Ch(')')))),

            OneOrMore(OneOrMore(Space()), NamedSection(Sequence(Attribute(), addAttribute()))) //TODO ###############################
        );
    }

    Rule Attribute() {
        return Sequence(
            push(new Attribute()),
            ZeroOrMore(Space()),
            OneOrMore(Letter()),
            pushAttributeName(),
            Optional(
                Ch(':'),
                OneOrMore(Space()),
                OneOrMore(
                    TestNot(Space()),
                    Any()),
                pushAttributeValue()),
            ZeroOrMore(Space()),
            Ch('('),
            OneOrMore(TestNot(FirstOf(Space(), Ch(')'))), Any()),
            pushAttributeType(),
            Ch(')'),
            Optional(
                OneOrMore(Space()),
                Ch('-'),
                OneOrMore(Space()),
                Line(),
                pushAttributeDescription())
        );
    }

    boolean pushAttributeName() {
        Attribute attr = (Attribute) pop();
        attr.setName(match().trim());
        push(attr);
        return true;
    }

    boolean pushAttributeValue() {
        Attribute attr = (Attribute) pop();
        attr.setValue(match().trim());
        push(attr);
        return true;
    }

    boolean pushAttributeType() {
        Attribute attr = (Attribute) pop();
        attr.setType(match().trim());
        push(attr);
        return true;
    }

    boolean pushAttributeDescription() {
        Attribute attr = (Attribute) pop();
        attr.setDescription(match().trim());
        push(attr);
        return true;
    }

    boolean addTypeDefinition() {
        AttributesSection section = (AttributesSection) pop();
        section.setTypeDefinition(match().trim());
        push(section);
        return true;
    }

    boolean addAttribute() {
        List<Attribute> list = (List<Attribute>) peek(1);
        list.add((Attribute) pop());
        return true;
    }

    //************* Asset section ****************

    Rule AssertDefinition(Rule keyword) {
        return NamedSection(keyword);
    }

    Rule AssertSection(Rule keyword, AssetSection section) {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(AssertDefinition(keyword)), push(section),
            AssertDefinition(keyword),

            EmptyLine(),
            Optional(CodeBlocks()),
            OneOrMore(TestNot(CodeBlocks()), Line(), NewLine()), setBody(),
            Optional(CodeBlocks())
        );
    }

    Rule CodeBlocks() {
        return Sequence(OneOrMore(Space()), String("```"), NewLine());
    }

    boolean setBody() {
        AssetSection section = (AssetSection) pop();
        String match = match();
        String[] split = match.split("\n");
        int i = sizeWhitespace(split[0]);
        String collect = Stream.of(split)
            .map(s -> s.substring(i, s.length()))
            .collect(Collectors.joining("\n"));
        section.setContent(collect);
        push(section);
        return true;
    }

    int sizeWhitespace(String str) {
        OptionalInt firstNotSpace = str.chars()
            .filter(c -> c != ' ' && c != '\t')
            .findFirst();
        if (firstNotSpace.isPresent())
            return str.indexOf(firstNotSpace.getAsInt());
        return 0;
    }

    //************* Keywords ****************

    Rule TestNotKeyword() {
        return TestNot(FirstOf(
            GroupNamed(),
            ResourceNamed()));
    }

    Rule TestKeyword() {
        return String("<keyword>");
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

    boolean debug(Object... v) {
        System.err.println("[DEBUG] " + match() + " " + Arrays.asList(v));
        return true;
    }

}