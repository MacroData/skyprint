package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class GroupSection extends Section {

    @Getter(lazy = true)
    private final List<ResourceSection> resources = lazy.list(ResourceSection.class);

    @Getter
    @Setter
    private String identifier;

    public GroupSection(String identifier) {
        this.identifier = identifier;
    }

}