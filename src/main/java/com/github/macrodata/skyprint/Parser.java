package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.*;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    Rule MetadataSection() {
        return Sequence(
            push(new MetadataSection()),

            Pair(
                Sequence(String("FORMAT"), push()),
                Sequence(MapValue(), push())),
            addToMap(),

            ZeroOrMore(
                Test(Pair(MapKey(), MapValue())),
                Pair(
                    Sequence(MapKey(), push()),
                    Sequence(MapValue(), push())),
                addToMap()));
    }

    //************* Abstract Section ****************

    Rule NamedSection(Rule body) {
        return Sequence(
            ZeroOrMore(Space()),
            FirstOf(
                FirstOf("######", "#####", "####", "###", "##", "#"),
                FirstOf("++++++", "+++++", "++++", "+++", "++", "+")),
            OneOrMore(Space()),
            body,
            NewLine());
    }

    Rule PayloadSection(Rule body) {
        return Sequence(
            NamedSection(body),

            ZeroOrMore(EmptyLine()),

            FirstOf(Sequence(
                Test(FirstOf(HeadersSection(), AttributesSection(), BodySection(), SchemaKeyword())),
                Optional(HeadersSection(), addAsChild()),
                ZeroOrMore(Space()),
                Optional(AttributesSection(), addAsChild()),
                ZeroOrMore(Space()),
                Optional(BodySection(), addAsChild()),
                ZeroOrMore(Space()),
                Optional(SchemaKeyword(), addAsChild()),
                ZeroOrMore(Space())
            ), Sequence(
                Optional(CodeBlocks()),
                ZeroOrMore(TestNot(CodeBlocks()), OneOrMore(Space()), Line(), NewLine()), setBodyPayload(),
                Optional(CodeBlocks())
            ))
        );
    }

    boolean aaa() {
        pop();
        return true;
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


    //************* API name & Overview Section ****************

    Rule OverviewSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(NamedSection(Line())),
            push(new OverviewSection()),
            NamedSection(Sequence(Line(), setField("name"))),

            OneOrMore(
                TestNotKeyword(),
                Any()),
            setField("description", match().trim().replace("\n", " ")),
            true
        );
    }

    //************* Resource group section ****************

    Rule GroupSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(NamedSection(Sequence(GroupKeyword(), Identifier()))),
            push(new GroupSection()),
            NamedSection(Sequence(GroupKeyword(), Identifier(), setField("identifier"))),
            Optional(
                OneOrMore(
                    TestNot(FirstOf(GroupNamed(), ResourceNamed())),
                    Any()),
                setField("description", match().trim().replace("\n", " "))),

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
                Sequence(
                    URITemplateKeyword(), setField("template")),
                Sequence(
                    HTTPMethodKeyword(), setField("method"),
                    OneOrMore(Space()), URITemplateKeyword(), setField("template")),
                Sequence(
                    Identifier(), setField("identifier"),
                    Ch('['), URITemplateKeyword(), setField("template"), Ch(']')),
                Sequence(
                    Identifier(), setField("identifier"),
                    Ch('['), HTTPMethodKeyword(), setField("method"),
                    OneOrMore(Space()), URITemplateKeyword(), setField("template"), Ch(']')))),

            ZeroOrMore(
                TestNot(FirstOf(ResourceNamed(), GroupNamed(), ActionNamed(), NamedSection(ResponseNamed()))),
                Any()),
            setField("description", match().trim().replace("\n", " ")),

            FirstOf(
                OneOrMore(Sequence(ActionSection(), addAsChild())),
                Sequence(ResponseSection(), addAction())
            )
        );
    }

    boolean addAction() {
        ActionSection action = new ActionSection();
        ResponseSection response = (ResponseSection) pop();
        action.setChildren(Arrays.asList(response));
        push(action);
        addAsChild();
        return true;
    }

    //************* Action section ****************

    Rule ActionSection() {
        return Sequence(
            ZeroOrMore(EmptyLine()),

            Test(ActionNamed()),
            push(new ActionSection()),
            NamedSection(FirstOf(
                Sequence(
                    HTTPMethodKeyword(), setField("method")),
                Sequence(
                    Identifier(), setField("identifier"),
                    Ch('['), HTTPMethodKeyword(), setField("method"), Ch(']')),
                Sequence(
                    Identifier(),
                    Ch('['), HTTPMethodKeyword(), setField("method"),
                    OneOrMore(Space()), URITemplateKeyword(), setField("template"), Ch(']')))),

            OneOrMore(
                TestNot(FirstOf(ActionNamed(), ResourceNamed(), GroupNamed(), ResponseSection())),
                Any()),
            setField("description", match().trim().replace("\n", " ")),

            OneOrMore(
                ZeroOrMore(EmptyLine()),
                ResponseSection(),
                addAsChild()),
            ZeroOrMore(EmptyLine())
        );
    }

    //************* Response Section ****************

    Rule ResponseNamed() {
        return Sequence(
            String("Response"), ZeroOrMore(Space()),
            OneOrMore(Digit()), ZeroOrMore(Space()),
            Optional(Ch('('), OneOrMore(TestNot(Ch(')')), Any()), Ch(')')));
    }

    Rule ResponseSection() {
        return PayloadSection(
            Sequence(
                Test(ResponseNamed()),
                push(new ResponseSection()),
                Sequence(
                    String("Response"), ZeroOrMore(Space()),
                    OneOrMore(Digit()), setField("httpStatusCode", Integer.parseInt(match().trim())), ZeroOrMore(Space()),
                    Optional(Ch('('), OneOrMore(TestNot(Ch(')')), Any()), setField("mediaType"), Ch(')')))
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
                Pair(Sequence(MapKey(), push()), Sequence(MapValue(), push())), addToMap())
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
                Optional(OneOrMore(Space()), Ch('('), Identifier(), setField("typeDefinition"), Ch(')')))),

            OneOrMore(OneOrMore(Space()), NamedSection(Sequence(Attribute(), addAttribute())))
        );
    }

    Rule Attribute() {
        return Sequence(
            push(new Attribute()),
            ZeroOrMore(Space()),
            OneOrMore(Letter()),
            setField("name"),
            Optional(
                Ch(':'),
                OneOrMore(Space()),
                OneOrMore(
                    TestNot(Space()),
                    Any()),
                setField("value")),
            ZeroOrMore(Space()),
            Ch('('),
            OneOrMore(TestNot(FirstOf(Space(), Ch(')'))), Any()),
            setField("type"),
            Ch(')'),
            Optional(
                OneOrMore(Space()),
                Ch('-'),
                OneOrMore(Space()),
                Line(),
                setField("description"))
        );
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
            OneOrMore(TestNot(CodeBlocks()), OneOrMore(Space()), Line(), NewLine()), setBody(),
            Optional(CodeBlocks())
        );
    }

    Rule CodeBlocks() {
        return Sequence(OneOrMore(Space()), String("```"), NewLine());
    }

    boolean setBodyPayload() {
        PayloadSection section = (PayloadSection) pop();
        String match = match();
        String[] split = match.split("\n");
        int i = sizeWhitespace(split[0]);
        String collect = Stream.of(split)
            .map(s -> s.substring(i, s.length()))
            .collect(Collectors.joining("\n"));
        try {
            Field body = PayloadSection.class.getDeclaredField("body");
            body.setAccessible(true);
            AtomicReference<String> o = (AtomicReference<String>) body.get(section);
            o.set(collect);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        push(section);
        return true;
    }

    boolean setBody() {
        AssetSection section = (AssetSection) pop();
        String match = match();
        String[] split = match.split("\n");
        int i = sizeWhitespace(split[0]);
        String collect = Stream.of(split)
            .map(s -> s.substring(i, s.length()))
            .collect(Collectors.joining("\n"));
        AssetSection clone = section.clone();
        clone.setContent(collect);
        push(clone);
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

    //************* Body section ****************

    Rule BodyKeyword() {
        return String("Body");
    }

    Rule BodySection() {
        return AssertSection(BodyKeyword(), new BodySection());
    }

    List stack() {
        return StreamSupport.stream(getContext().getValueStack().spliterator(), false).collect(Collectors.toList());
    }

    //************* Schema section ****************

    Rule SchemaKeyword() {
        return String("Schema");
    }

    Rule SchemaSection() {
        return AssertSection(SchemaKeyword(), new SchemaSection());
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