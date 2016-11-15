package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.MetadataSection;
import org.json.JSONException;
import org.parboiled.Parboiled;
import org.parboiled.support.ParsingResult;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.github.macrodata.skyprint.TestHelper.sampleReader;
import static com.github.macrodata.skyprint.TestHelper.toJsonString;


public class ParserRulesTest {
    private Parser parser;

    @BeforeClass
    public void setUp() {
        parser = Parboiled.createParser(Parser.class);
    }

    @Test
    public void testMetadataSectionCorrectParsing() throws IOException, JSONException {

        String metadataSumple = sampleReader("/samples/metadata/full_section.md");
        ParsingResult<?> result = ParserHelper.parse(parser.MetadataSection(), metadataSumple);

        MetadataSection section = (MetadataSection) result.resultValue;
        Assert.assertNotNull(section);
        JSONAssert.assertEquals(toJsonString(section), sampleReader("/expected/metadata/full_section.json"), true);
    }

    @Test
    public void testMetadataSectionCorrectParsingWithoutHost() throws IOException, JSONException {

        String metadataSumple = sampleReader("/samples/metadata/without_host_section.md");
        ParsingResult<?> result = ParserHelper.parse(parser.MetadataSection(), metadataSumple);

        MetadataSection section = (MetadataSection) result.resultValue;
        Assert.assertNotNull(section);
        JSONAssert.assertEquals(toJsonString(section), sampleReader("/expected/metadata/without_host_section.json"), true);
    }


}
