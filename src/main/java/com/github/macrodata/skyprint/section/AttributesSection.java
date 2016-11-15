package com.github.macrodata.skyprint.section;

import com.github.macrodata.skyprint.Attribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AttributesSection extends Section implements List<Attribute> {

    @Delegate
    private final List<Attribute> attributes = new ArrayList<>();

    @Getter
    private String typeDefinition;

}
