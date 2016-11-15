package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ResponseSection extends PayloadSection {

    @Getter
    private Integer httpStatusCode;

    @Getter
    private String mediaType;

}