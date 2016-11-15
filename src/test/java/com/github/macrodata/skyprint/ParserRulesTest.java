package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.MetadataSection;
import org.json.JSONException;
import org.parboiled.Parboiled;
import org.parboiled.support.ParsingResult;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class ParserRulesTest {

    private Parser parser;

    @BeforeClass
    public void setUp() {
        parser = Parboiled.createParser(Parser.class);
    }

    @DataProvider
    public Object[][] samplesMetadataSection() {
        return new Object[][]{
            {"metadata/simple"},
            {"metadata/full"}
        };
    }

    @Test(dataProvider = "samplesMetadataSection")
    public void testMetadataSection(String resource) throws IOException, JSONException {
        String sample = sample(resource + ".md");
        String expected = expected(resource + ".json");

        ParsingResult<?> result = TestHelper.parse(parser.MetadataSection(), sample);

        MetadataSection section = (MetadataSection) result.resultValue;
        Assert.assertNotNull(section);
        JSONAssert.assertEquals(expected, TestHelper.toJson(section), true);
    }

    private static String sample(String resource) throws IOException {
        return TestHelper.resource("/samples/" + resource);
    }

    private static String expected(String resource) throws IOException {
        return TestHelper.resource("/expected/" + resource);
    }

}
