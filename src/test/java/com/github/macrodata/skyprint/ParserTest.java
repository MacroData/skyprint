package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.*;
import org.mockito.Mockito;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.support.ParsingResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParserTest {

    private Parser parser;

    @BeforeClass
    public void setUp() {
        parser = Parboiled.createParser(Parser.class);
    }

    @DataProvider
    public Object[][] samplesMetadataSection() {
        return new Object[][]{
            {
                "FORMAT: 1A\n",
                map(tuple("FORMAT", "1A"))},
            {
                "FORMAT: 1A\nHOST: https://alpha-api.app.net\n\nDescription",
                map(tuple("FORMAT", "1A"), tuple("HOST", "https://alpha-api.app.net"))},
            {
                "FORMAT: 1A\nHOST: https://alpha-api.app.net\nORGANIZATION: MacroData\nDescription",
                map(tuple("FORMAT", "1A"), tuple("HOST", "https://alpha-api.app.net"), tuple("ORGANIZATION", "MacroData"))}
        };
    }

    @Test(dataProvider = "samplesMetadataSection")
    public void testMetadataSection(String sample, Map<String, String> expected) {
        ParsingResult<?> result = TestHelper.parse(parser.MetadataSection(), sample);

        MetadataSection section = (MetadataSection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertEquals(section, expected);
    }

    @DataProvider
    public Object[][] samplesOverviewSection() {
        return new Object[][]{
            {
                "# Basic ACME Blog API\nWelcome to the **ACME Blog** API.\n\nThis API provides access to the " +
                    "**ACME Blog** service.\n",
                Pattern.compile("Basic.*API"), Pattern.compile("Welcome.*service\\.")},
            {
                "# The Simplest API\nThis is one of the simplest APIs written in the **API Blueprint**.\n" +
                    "# GET /message\n+ Response 200 (text/plain)\n",
                Pattern.compile("The Simplest API"), Pattern.compile("This is.*Blueprint\\*\\*\\.")},
        };
    }

    @Test(dataProvider = "samplesOverviewSection")
    public void testOverviewSection(String sample, Pattern namePattern, Pattern descriptionPattern) {
        ParsingResult<?> result = TestHelper.parse(parser.OverviewSection(), sample);

        OverviewSection section = (OverviewSection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertTrue(namePattern.matcher(section.getName()).matches());
        Assert.assertTrue(descriptionPattern.matcher(section.getDescription()).matches());
    }

    @DataProvider
    public Object[][] samplesNamedSection() {
        return new Object[][]{
            {"# /posts/{id}\n", parser.ResourceNamed()},
            {"## Blog Posts [/posts/{id}]\n", parser.ResourceNamed()},
            {"# GET /posts/{id}\n", parser.ResourceNamed()},
            {"# Blog Posts [GET /posts/{id}]\n", parser.ResourceNamed()},
            {"# Group Blog Posts\n", parser.GroupNamed()},
            {"# Group Authors\n", parser.GroupNamed()}
        };
    }

    @Test(dataProvider = "samplesNamedSection")
    public void testResourceNamed(String sample, Rule rule) {
        String result = TestHelper.match(rule, sample);

        Assert.assertEquals(sample, result);
    }

    @DataProvider
    public Object[][] samplesIdentifier() {
        return new Object[][]{
            {"Adam's Message 42", true},
            {"my-awesome-message_2", true},
            {"Adam's Message\n42", false},
            {"my-awesome-message_2 [", false},
            {"# Blog Posts [/posts/{id}]", false}
        };
    }

    @Test(dataProvider = "samplesIdentifier")
    public void testIdentifier(String sample, boolean expected) {
        String result = TestHelper.match(parser.Identifier(), sample);

        Assert.assertEquals(sample.equals(result), expected);
    }

    @DataProvider
    public Object[][] samplesURITemplate() {
        return new Object[][]{
            {"/path/to/resources/42"},
            {"/path/to/resources/{var}"},
            {"/coupons{?limit}"},
            {"/message"}
        };
    }

    @Test(dataProvider = "samplesURITemplate")
    public void testURITemplate(String sample) {
        String result = TestHelper.match(parser.URITemplateKeyword(), sample);

        Assert.assertEquals(result, sample);
    }

    @DataProvider
    public Object[][] samplesMultipleVariables() {
        return new Object[][]{
            {"{var}"},
            {"{var1,var2,var3}"},
            {"{#var}"},
            {"{+var}"},
            {"{?var}"},
            {"{?var1,var2}"},
            {"{?%24var}"},
            {"{&var}"}
        };
    }

    @Test(dataProvider = "samplesMultipleVariables")
    public void testMultipleVariables(String sample) {
        String result = TestHelper.match(parser.MultipleVariables(), sample);

        Assert.assertEquals(result, sample);
    }

    @DataProvider
    public Object[][] samplesHeadersSection() {
        return new Object[][]{
            {
                "+ Headers\n\n" +
                    "        Accept-Charset: utf-8\n" +
                    "        Connection: keep-alive\n" +
                    "        Content-Type: multipart/form-data, boundary=AaB03x\n",
                map(tuple("Accept-Charset", "utf-8"), tuple("Connection", "keep-alive"),
                    tuple("Content-Type", "multipart/form-data, boundary=AaB03x"))}
        };
    }

    @Test(dataProvider = "samplesHeadersSection")
    public void testHeadersSection(String sample, Map<String, String> expected) {
        ParsingResult<?> result = TestHelper.parse(parser.HeadersSection(), sample);

        Assert.assertEquals(result.resultValue, expected);
    }

    @DataProvider
    public Object[][] samplesAttributesSection() {
        return new Object[][]{
            {
                "+ Attributes (object)\n" +
                    "    + id (number)\n" +
                    "    + message (string) - The blog post article\n" +
                    "    + author: john@appleseed.com (string) - Author of the blog post\n",
                "object",
                Arrays.asList(
                    attribute("id", null, "number", null),
                    attribute("message", null, "string", "The blog post article"),
                    attribute("author", "john@appleseed.com", "string", "Author of the blog post"))
            }
        };
    }

    @Test(dataProvider = "samplesAttributesSection")
    public void testsAttributesSection(String sample, String typeDefinition, List<Attribute> attributes) {
        ParsingResult<?> result = TestHelper.parse(parser.AttributesSection(), sample);

        AttributesSection section = (AttributesSection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertEquals(section, attributes);
        Assert.assertEquals(section.getTypeDefinition(), typeDefinition);
    }

    @DataProvider
    public Object[][] samplesAssertSection() {
        return new Object[][]{
            {
                "+ <keyword>\n" +
                    "\n" +
                    "        {\n" +
                    "            \"message\": \"Hello\"\n" +
                    "        }\n",
                "{\n    \"message\": \"Hello\"\n}"},
            {
                "+ <keyword>\n" +
                    "\n" +
                    "    ```\n" +
                    "    {\n" +
                    "        \"message\": \"Hello\"\n" +
                    "    }\n" +
                    "    ```\n",
                "{\n    \"message\": \"Hello\"\n}"}
        };
    }

    @Test(dataProvider = "samplesAssertSection")
    public void testAssertSection(String sample, String expected) {
        AssetSection spyAssetSection = Mockito.spy(AssetSection.class);

        ParsingResult<?> result = TestHelper.parse(
            parser.AssertSection(parser.TestKeyword(), spyAssetSection), sample);

        AssetSection section = (AssetSection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertEquals(section.getContent(), expected);
    }

    @DataProvider
    public Object[][] sampleBodySection() {
        return new Object[][]{
            {"      + Body\n" +
                "\n" +
                "           {\n" +
                "               \"message\": \"Hello\"\n" +
                "           }\n", "{\n    \"message\": \"Hello\"\n}"}
        };
    }

    @Test(dataProvider = "sampleBodySection")
    public void testBodySection(String sample, String expected) {
        ParsingResult<?> result = TestHelper.parse(parser.BodySection(), sample);

        BodySection section = (BodySection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertEquals(section.getContent(), expected);
    }

    @DataProvider
    public Object[][] sampleSchemaSection() {
        return new Object[][]{
            {"      + Schema\n" +
                "\n" +
                "           {\n" +
                "               \"message\": \"Hello\"\n" +
                "           }\n", "{\n    \"message\": \"Hello\"\n}"}
        };
    }

    @Test(dataProvider = "sampleSchemaSection")
    public void testSchemaSection(String sample, String expected) {
        ParsingResult<?> result = TestHelper.parse(parser.SchemaSection(), sample);

        SchemaSection section = (SchemaSection) result.resultValue;
        Assert.assertNotNull(section);
        Assert.assertEquals(section.getContent(), expected);
    }

    @SafeVarargs
    private static Map<String, String> map(Map.Entry<String, String>... tuples) {
        return Arrays.stream(tuples)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> tuple(String key, String value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    private static Attribute attribute(String name, String value, String type, String description) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setValue(value);
        attribute.setType(type);
        attribute.setDescription(description);
        return attribute;
    }

}