package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static com.github.macrodata.skyprint.section.SectionHelper.lazy;
import static com.github.macrodata.skyprint.section.SectionHelper.list;

@ToString
public class GroupSection extends Section {

    @Getter
    @Setter
    private String identifier;

    @Setter
    private List<ResourceSection> resources;

    public GroupSection(String identifier) {
        this.identifier = identifier;
    }

    public List<ResourceSection> getResources() {
        if (resources == null)
            lazy(this::setResources, list(this, ResourceSection.class));
        return resources;
    }

}