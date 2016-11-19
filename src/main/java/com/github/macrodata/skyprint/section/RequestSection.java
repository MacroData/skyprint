package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

@ToString
public class RequestSection extends PayloadSection {

    @Getter
    private String identifier;

    @Getter
    private String mediaType;

}