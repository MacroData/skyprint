package com.github.macrodata.skyprint.section;

import com.github.macrodata.skyprint.Attribute;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AttributesSection extends Section implements List<Attribute> {

    @Getter
    @Setter
    private String typeDefinition;

    @Delegate
    private List<Attribute> attributes;

    public AttributesSection() {
        this.attributes = new ArrayList<>();
    }

}
