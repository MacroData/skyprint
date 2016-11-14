package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.RootSection;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.json.JSONException;
import org.parboiled.support.ParsingResult;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.*;
import java.util.stream.Collectors;

public class SkyPrintProcessorTest {

    private static final ObjectMapper JSON_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        JSON_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }

    private final String apibResource;
    private final String msonResource;

    @Factory(dataProvider = "samples")
    public SkyPrintProcessorTest(String resource) {
        this.apibResource = "/apib/" + resource + ".md";
        this.msonResource = "/mson/" + resource + ".json";
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
            {"15. Advanced JSON Schema"}
        };
    }

    @DataProvider
    public Object[][] samplesMSON() {
        return new Object[][]{
            {resource(apibResource), resource(msonResource)}
        };
    }

    @Test(dataProvider = "samplesMSON")
    public void testParse(String apib, String mson) throws IOException, JSONException {
        ParsingResult<RootSection> result = new SkyPrintProcessor().parse(apib.toCharArray());

        String json = JSON_MAPPER.writerWithDefaultPrettyPrinter()
            .writeValueAsString(result.resultValue);

        JSONAssert.assertEquals(mson, json, true);
    }

    private static String resource(String resource) {
        InputStream stream = Object.class.getResourceAsStream(resource);
        if (stream == null) return "";
        return new BufferedReader(new InputStreamReader(stream))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    private static void save(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

}