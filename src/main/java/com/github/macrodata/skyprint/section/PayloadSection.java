package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public abstract class PayloadSection extends Section {

    @Getter
    @Setter
    private Map<String, String> headers;

    private AttributesSection attributes;

    private BodySection body;


}
