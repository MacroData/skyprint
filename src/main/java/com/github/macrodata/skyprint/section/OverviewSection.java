package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * https://apiblueprint.org/documentation/specification.html#api-name--overview-section
 */
@ToString
public class OverviewSection extends Section {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String description;

    public OverviewSection(String name) {
        this.name = name;
    }

}