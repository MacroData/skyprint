package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.Section;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

import java.util.List;

class AbstractParser extends BaseParser<Section> {

    boolean addAsChild() {
        Section parent = peek(1);
        List<Section> children = parent.getChildren();
        Section section = pop();
        section.setParent(parent);
        children.add(section);
        return true;
    }

    //************* Line ****************

    @SuppressSubnodes
    Rule Line() {
        return OneOrMore(TestNot(NewLine()), Any());
    }

    Rule EmptyLine() {
        return NewLine();
    }

    //************* Char ****************

    @SuppressNode
    Rule Any() {
        return ANY;
    }

    Rule Space() {
        return AnyOf(" \t");
    }

    Rule NewLine() {
        return AnyOf("\n\r");
    }

    Rule Empty() {
        return EMPTY;
    }

}