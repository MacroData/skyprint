package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class ResourceSection extends Section {

    @Getter(lazy = true)
    private final List<ActionSection> actions = lazy.list(ActionSection.class);

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