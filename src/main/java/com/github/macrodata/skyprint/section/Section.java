package com.github.macrodata.skyprint.section;

import lombok.Getter;
import lombok.Setter;
import org.parboiled.trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Section implements TreeNode<Section> {

    @Getter
    @Setter
    protected List<Section> children = new ArrayList<>();

    @Getter
    @Setter
    protected Section parent;

}