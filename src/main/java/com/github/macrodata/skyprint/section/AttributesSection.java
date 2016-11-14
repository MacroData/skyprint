package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AttributesSection extends Section implements List<String> {

    @Getter
    @Setter
    private String typeDefinition;

    @Delegate
    private List<String> attributes;

    public AttributesSection() {
        this.attributes = new ArrayList<>();
    }

}
