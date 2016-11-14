package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ActionSection extends Section {

    @Getter
    private String identifier;

    @Getter
    private String template;

    @Getter
    private String method;

    @Getter
    private String description;

}
