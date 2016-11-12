package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.MetadataSection;
import org.parboiled.Parboiled;
import org.parboiled.support.ParsingResult;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ParserTest {

    private Parser parser;

    @BeforeMethod
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
        ParsingResult<?> result = ParserHelper.parse(parser.MetadataSection(), sample);

        MetadataSection section = (MetadataSection) result.resultValue;
        Assert.assertEquals(section, expected);
    }

    @SafeVarargs
    private static Map<String, String> map(Map.Entry<String, String>... tuples) {
        return Arrays.stream(tuples)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> tuple(String key, String value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

}