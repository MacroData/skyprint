package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class AssetSection extends Section implements Cloneable {

    @Getter
    @Setter
    private String content;

    @Override
    public AssetSection clone() {
        try {
            return (AssetSection) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}