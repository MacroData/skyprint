package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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

    @Setter
    private List<ActionSection> actions;

    public List<ActionSection> getActions() {
        if (actions == null)
            setActions(SectionHelper.list(this, ActionSection.class));
        return actions;
    }

}