package com.github.macrodata.skyprint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Attribute {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String value;

    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private String description;

}
