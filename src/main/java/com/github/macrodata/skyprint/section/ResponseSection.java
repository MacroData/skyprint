package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ResponseSection extends Section {

    @Getter
    @Setter
    private Integer httpStatusCode;

    @Getter
    @Setter
    private String mediaType;

    @Getter
    @Setter
    private String payload;

}
