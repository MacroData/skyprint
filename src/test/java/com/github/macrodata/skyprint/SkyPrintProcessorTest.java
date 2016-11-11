package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.RootSection;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SkyPrintProcessorTest {

    @Test
    public void test() {
        InputStream stream = Object.class.getResourceAsStream("/01. Simplest API.md");
        String source = new BufferedReader(new InputStreamReader(stream))
            .lines()
            .collect(Collectors.joining("\n"));

        ParsingResult<RootSection> result = new SkyPrintProcessor().parse(source.toCharArray());
        String tree = ParseTreeUtils.printNodeTree(result);

        Assert.assertNotNull(tree);
        Assert.assertFalse(tree.isEmpty());
    }

}