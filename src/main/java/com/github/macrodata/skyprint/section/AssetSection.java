package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class AssetSection extends Section {

    @Getter
    @Setter
    private String content;

}