package com.github.macrodata.skyprint.section;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class SectionHelper {

    private SectionHelper() {
        super();
    }

    static <T extends Section> List<T> list(Section section, Class<T> cls) {
        return section.getChildren().stream()
            .filter(child -> child.getClass().equals(cls))
            .map(c -> (T) c)
            .collect(Collectors.toList());
    }

    static <T extends Section> T get(Section section, Class<T> cls) {
        return section.getChildren().stream()
            .filter(child -> child.getClass().equals(cls))
            .map(c -> (T) c)
            .findFirst()
            .orElse(null);
    }

    static <T> void lazy(Consumer<T> consumer, T o) {
        consumer.accept(o);
    }

}