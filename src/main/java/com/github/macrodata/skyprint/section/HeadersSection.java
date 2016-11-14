package com.github.macrodata.skyprint.section;

import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
public class HeadersSection extends Section implements Map<String, String> {

    @Delegate
    private Map<String, String> headers;

    public HeadersSection() {
        this.headers = new LinkedHashMap<>();
    }

}