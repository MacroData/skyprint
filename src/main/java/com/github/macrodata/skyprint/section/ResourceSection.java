package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class ResourceSection extends Section {

    @Getter(lazy = true)
    private final List<ActionSection> actions = lazy.list(ActionSection.class);

    @Getter
    private String identifier;

    @Getter
    private String template;

    @Getter
    private String method;

    @Getter
    private String description;

}