package com.github.macrodata.skyprint.section;

import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
public class HeadersSection extends Section implements Map<String, String> {

    @Delegate
    private final Map<String, String> headers = new LinkedHashMap<>();

}