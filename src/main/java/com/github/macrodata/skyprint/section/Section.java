package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.parboiled.trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Section implements TreeNode<Section> {

    @JsonIgnore
    final LazyEvaluation lazy = LazyEvaluation.of(this);

    @Getter
    @Setter
    @JsonIgnore
    private List<Section> children = new ArrayList<>();

    @Getter
    @Setter
    @JsonIgnore
    private Section parent;

}