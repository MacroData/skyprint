package com.github.macrodata.skyprint.section;

import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * https://apiblueprint.org/documentation/specification.html#def-metadata-section
 */
@ToString
public class MetadataSection extends Section implements Map<String, String> {

    @Delegate()
    private final Map<String, String> metadata;

    public MetadataSection() {
        this.metadata = new LinkedHashMap<>();
    }

}