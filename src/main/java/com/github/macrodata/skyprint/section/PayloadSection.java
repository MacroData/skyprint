package com.github.macrodata.skyprint.section;

import com.github.macrodata.skyprint.Attribute;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public abstract class PayloadSection extends AssetSection {

    @Getter(lazy = true)
    private final Map<String, String> headers = lazy.object(HeadersSection.class).orElse(null);

    @Getter(lazy = true)
    private final List<Attribute> attributes = lazy.object(AttributesSection.class).orElse(null);

    @Getter(lazy = true)
    private final String body = lazy.object(BodySection.class).map(AssetSection::getContent).orElse(null);

    @Getter(lazy = true)
    private final String schema = lazy.object(SchemaSection.class).map(AssetSection::getContent).orElse(null);

}
