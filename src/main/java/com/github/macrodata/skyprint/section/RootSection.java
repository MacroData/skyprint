package com.github.macrodata.skyprint.section;

import lombok.Setter;
import lombok.ToString;

import javax.xml.transform.stream.StreamSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.macrodata.skyprint.section.SectionHelper.*;

@ToString
public class RootSection extends Section {

    @Setter
    private Map<String, String> metadata;

    @Setter
    private String name;

    @Setter
    private List<ResourceSection> resources;

    @Setter
    private List<GroupSection> groups;

    public Map<String, String> getMetadata() {
        if (metadata == null)
            lazy(this::setMetadata, get(this, MetadataSection.class));
        return metadata;
    }

    public String getName() {
        if (name == null)
            lazy(this::setName, get(this, OverviewSection.class).getName());
        return name;
    }

    @Override
    public String getDescription() {
        if (super.getDescription() == null)
            lazy(this::setDescription, get(this, OverviewSection.class).getDescription());
        return super.getDescription();
    }

    public List<ResourceSection> getResources() {
        if (resources == null)
            lazy(this::setResources, list(this, ResourceSection.class));
        return resources;
    }

    public List<GroupSection> getGroups() {
        if (groups == null)
            lazy(this::setGroups, list(this, GroupSection.class));
        return groups;
    }

}