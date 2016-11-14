package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

@ToString
public class OverviewSection extends Section {

    @Getter
    private String name;

    @Getter
    private String description;

}