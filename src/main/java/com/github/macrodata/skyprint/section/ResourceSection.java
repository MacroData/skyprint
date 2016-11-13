package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ResourceSection extends Section {

    @Getter
    @Setter
    private String identifier;

    @Getter
    @Setter
    private String template;

    @Getter
    @Setter
    private String method;

}