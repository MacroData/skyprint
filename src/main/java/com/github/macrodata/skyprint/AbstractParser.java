package com.github.macrodata.skyprint;

import com.github.macrodata.skyprint.section.Section;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class AbstractParser extends BaseParser<Object> {

    boolean addAsChild() {
        Section parent = (Section) peek(1);
        List<Section> children = parent.getChildren();
        Section section = (Section) pop();
        section.setParent(parent);
        children.add(section);
        return true;
    }

    //************* Map ****************

    boolean addToMap() {
        Object value = pop();
        Object key = pop();
        Map map = (Map) peek();
        map.put(key, value);
        return true;
    }

    Rule Pair(Rule key, Rule value) {
        return Sequence(key, Ch(':'), value, NewLine());
    }

    Rule MapKey() {
        return OneOrMore(TestNot(FirstOf(Ch(':'), NewLine())), Any());
    }

    Rule MapValue() {
        return Line();
    }

    boolean push() {
        return push(match().trim());
    }

    //************* Fields ****************

    boolean setField(String fieldName) {
        return setField(peek().getClass(), fieldName, match().trim());
    }

    boolean setField(String fieldName, String value) {
        return setField(peek().getClass(), fieldName, value);
    }

    boolean setField(Class cls, String fieldName, String value) {
        final Object obj = peek();
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule Letter() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

}