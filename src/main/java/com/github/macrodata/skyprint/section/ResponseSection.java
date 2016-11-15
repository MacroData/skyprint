package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ResponseSection extends Section {

    @Getter
    private Integer httpStatusCode;

    @Getter
    private String mediaType;

    @Getter
    private String payload;

}