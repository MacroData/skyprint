package com.github.macrodata.skyprint;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class SkyPrintProcessorTest {

    private SkyPrintProcessor processor;

    @BeforeClass
    public void setUp() {
        processor = new SkyPrintProcessor();
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"01. Simplest API"},
            {"02. Resource and Actions"},
            {"03. Named Resource and Actions"},
            {"04. Grouping Resources"},
            {"05. Responses"},
            {"06. Requests"},
            {"07. Parameters"},
            {"08. Attributes"},
            {"09. Advanced Attributes"},
            {"10. Data Structures"},
            {"11. Resource Model"},
            {"12. Advanced Action"},
            {"13. Named Endpoints"},
            {"14. JSON Schema"},
            {"15. Advanced JSON Schema"},
        };
    }

    @Test(dataProvider = "samples")
    public void testParseToJson(String resource) throws IOException, JSONException {
        final String apib = TestHelper.resource("/apib/" + resource + ".md");
        final String json = TestHelper.resource("/json/" + resource + ".json");

        String result = processor.parseToJson(apib);

        System.out.println(result);
        Assert.assertNotNull(result);
        JSONAssert.assertEquals(json, result, true);
    }

}