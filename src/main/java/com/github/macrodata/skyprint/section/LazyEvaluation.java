package com.github.macrodata.skyprint.section;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "of")
class LazyEvaluation {

    private final Section section;

    public <T extends Section> Optional<T> object(Class<T> cls) {
        return stream(cls)
            .findFirst();
    }

    public <T extends Section> List<T> list(Class<T> cls) {
        return stream(cls)
            .collect(Collectors.toList());
    }

    private <T> Stream<T> stream(Class<T> cls) {
        return section.getChildren().stream()
            .filter(child -> child.getClass().equals(cls))
            .map(c -> (T) c);
    }

}