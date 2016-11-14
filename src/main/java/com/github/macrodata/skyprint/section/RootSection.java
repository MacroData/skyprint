package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
public class RootSection extends Section {

    @Getter(lazy = true)
    private final Map<String, String> metadata = lazy.object(MetadataSection.class).orElse(null);

    @Getter(lazy = true)
    private final String name = lazy.object(OverviewSection.class).map(OverviewSection::getName).orElse(null);

    @Getter(lazy = true)
    private final String description = lazy.object(OverviewSection.class).map(OverviewSection::getDescription).orElse(null);

    @Getter(lazy = true)
    private final List<ResourceSection> resources = lazy.list(ResourceSection.class);

    @Getter(lazy = true)
    private final List<GroupSection> groups = lazy.list(GroupSection.class);

}