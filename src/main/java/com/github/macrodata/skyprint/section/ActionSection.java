package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class ActionSection extends Section {

    @Getter(lazy = true)
    private final List<ResponseSection> responses = lazy.list(ResponseSection.class);

    @Getter
    private String identifier;

    @Getter
    private String template;

    @Getter
    private String method;

    @Getter
    private String description;

}
