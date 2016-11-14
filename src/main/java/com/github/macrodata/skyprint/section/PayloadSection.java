package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;

public abstract class PayloadSection extends Section {

    @Getter
    @Setter
    private HeadersSection headers;

    private AttributesSection attributes;

    private BodySection body;


}
