package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.Section;
import org.mockito.Mockito;
import org.parboiled.Context;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.DefaultValueStack;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class AbstractParserTest {

    private AbstractParser parser;

    @BeforeMethod
    public void setUp() {
        parser = new AbstractParser();
    }

    @Test
    public void testAddAsChild() {
        Context<Object> mockContext = Mockito.mock(Context.class);
        DefaultValueStack spyStack = Mockito.spy(DefaultValueStack.class);
        Mockito.when(mockContext.getMatcher()).thenReturn(Mockito.mock(Matcher.class));
        Mockito.when(mockContext.getValueStack()).thenReturn(spyStack);

        Section root = new Section();
        Section child = new Section();
        parser.setContext(mockContext);
        parser.push(root);
        parser.push(child);

        parser.addAsChild();

        Assert.assertEquals(spyStack.size(), 1);
        Assert.assertFalse(root.getChildren().isEmpty());
        Assert.assertEquals(root.getChildren().get(0), child);
    }

    @DataProvider
    public Object[][] samplesLine() {
        return new Object[][]{
            {"aaa", "aaa"}, {"aaa\nbbb", "aaa"}
        };
    }

    @Test(dataProvider = "samplesLine")
    public void testLine(String sample, String expected) {
        String result = ParserHelper.match(parser.Line(), sample);

        Assert.assertEquals(result, expected);
    }

}