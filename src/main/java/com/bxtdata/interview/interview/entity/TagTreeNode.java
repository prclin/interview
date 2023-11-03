package com.bxtdata.interview.interview.entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TagTreeNode {
    private String key;
    private List<TagTreeNode> children;
    private Set<String> names;



    public TagTreeNode(String key) {
        this.key = key;
    }

    public TagTreeNode(String key, Set<String> names) {
        this.key = key;
        this.names = names;
    }

    public TagTreeNode() {
    }

    public String getKey() {
        if (key==null) return "";
        return key;
    }

    public void addChild(TagTreeNode node){
        if (children==null) children=new LinkedList<>();
        children.add(node);
    }

    public void addNames(Set<String> names){
        this.names.addAll(names);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<TagTreeNode> getChildren() {
        if (children==null) children=new LinkedList<>();
        return children;
    }

    public void setChildren(List<TagTreeNode> children) {
        this.children = children;
    }

    public Set<String> getNames() {
        if (names==null) names=new HashSet<>();
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }
}
